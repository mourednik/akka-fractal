package fractal

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import org.scalatest.BeforeAndAfterAll
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import akka.util.Timeout

class WorkerTest extends TestKit(ActorSystem("WorkerTest"))
  with ImplicitSender
  with WordSpec
  with BeforeAndAfterAll
  with MustMatchers {

  implicit val timeout = Timeout(10 seconds)
  override def afterAll() {
    system.shutdown()
  }

  "Worker" should {
    "work" in {
      val master = system.actorOf(Props[Master], "master")
      val worker = system.actorOf(Props(new Worker(master)))
      val task = makeTask
      val future = master ? task
      try {
        val result = Await.result(future, timeout.duration).asInstanceOf[ImageSegment]
      } catch {
        case e: Exception => fail(s"This attempt threw exception: $e")
      }
    }
  }

  def makeTask = {
    val dimension = Dimension(640, 480)
    val location = DefaultParameters.mandelbrotLocation
    val mandelbrotParameters = DefaultParameters.mandelbrotParameters
    RenderParams(dimension, location, mandelbrotParameters)
  }
}