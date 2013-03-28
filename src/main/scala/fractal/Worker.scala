package fractal

import scala.concurrent.Future
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import akka.pattern.pipe
import akka.actor.ActorRef

class Worker(master: ActorRef) extends Actor with ActorLogging {

  import MasterWorkerProtocol._

  private case class WorkComplete(result: WorkResult)

  private implicit val ec = context.dispatcher
  private val NUM_THREADS = Runtime.getRuntime.availableProcessors()
  private val mandelbrotRenderer = new Renderer(context.system) with MandelbrotAlgorithm
  private val juliaRenderer = new Renderer(context.system) with JuliaAlgorithm

  override def preStart() {
    master ! WorkerCreated(self)
  }

  private def processWork(work: Work) =
    work.subtask.renderParams.algorithmParams match {
      case _: MandelbrotParams => WorkResult(work, mandelbrotRenderer.render(NUM_THREADS, work.subtask))
      case _: JuliaParams      => WorkResult(work, juliaRenderer.render(NUM_THREADS, work.subtask))
    }

  private def doWork(work: Work) = Future {
    WorkComplete(processWork(work))
  } pipeTo self

  private def working(work: Work): Receive = {
    case WorkIsReady          =>
    case NoWorkToBeDone       =>
    case WorkToBeDone(_)      =>
    case WorkComplete(result) =>
      master ! WorkIsDone(self, result)
      master ! WorkerRequestsWork(self)
      context.become(idle)
  }

  private def idle: Receive = {
    case WorkIsReady        =>
      master ! WorkerRequestsWork(self)
    case WorkToBeDone(work) =>
      doWork(work)
      context.become(working(work))
    case NoWorkToBeDone     =>
  }

  def receive = idle
}
