package fractal

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Failure
import scala.util.Success

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSpec
import org.scalatest.matchers.MustMatchers

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout

class DistributedRendererTest extends FunSpec with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem("FractalSystem")
  implicit val timeout = Timeout(10 seconds)

  override def afterAll {
    system.shutdown()
  }

  describe("Worker") {
    it("should complete a job") {
      val task = makeSubTask
      val work = Work(system.deadLetters, task)
      val worker = TestActorRef[Worker]
      val future = worker ? work
      val result = future.value.get
      result.isSuccess must be(true)
    }
  }

  describe("Master") {
    val master = TestActorRef[Master]

    it("completes one render task") {
      val task = makeTask
      val future = master ? task
      try {
        Await.result(future, timeout.duration).asInstanceOf[ImageSegment]
      } catch {
        case e: Exception => fail(s"This attempt threw exception: $e")
      }
    }

    it("completes concurrent render tasks") {
      val taskSequence = makeTaskSequence
      val futures = taskSequence.map(task => master ? task)

      for (i <- 0 until futures.length) {
        futures(i) onComplete {
          case Success(result: ImageSegment) =>
            FileUtil.saveAsPNG(s"/tmp/julia$i.png", result.getBufferedImage)
          case Failure(failure) => println(s"$i failed: $failure")
          case _ =>
        }
      }
      Thread.sleep(3000)
    }
  }

  def makeTask = {
    val dimension = Dimension(640, 480)
    val location = DefaultParameters.mandelbrotLocation
    val mandelbrotParameters = DefaultParameters.mandelbrotParameters
    RenderParams(dimension, location, mandelbrotParameters)
  }

  def makeSubTask = {
    val dimension = Dimension(640, 480)
    val location = DefaultParameters.mandelbrotLocation
    val mandelbrotParameters = DefaultParameters.mandelbrotParameters
    new SubTask(RenderParams(dimension, location, mandelbrotParameters), 0, (0, 480), 0)
  }

  def makeTaskSequence = {
    val dimension = Dimension(640, 480)
    val location = DefaultParameters.juliaLocation
    val juliaParameters = DefaultParameters.juliaParameters
    val startTask = RenderParams(dimension, location, juliaParameters)
    RenderSequence.makeSequence(startTask, 4.0, 24)
  }
}
