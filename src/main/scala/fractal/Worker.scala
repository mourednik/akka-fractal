package fractal

import scala.concurrent.Future
import MasterWorkerProtocol.NoWorkToBeDone
import MasterWorkerProtocol.WorkIsDone
import MasterWorkerProtocol.WorkIsReady
import MasterWorkerProtocol.WorkToBeDone
import MasterWorkerProtocol.WorkerCreated
import MasterWorkerProtocol.WorkerRequestsWork
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.actorRef2Scala
import akka.pattern.pipe
import akka.actor.ActorRef

class Worker(master: ActorRef) extends Actor with ActorLogging {

  import MasterWorkerProtocol._

  private val NUM_THREADS = Runtime.getRuntime().availableProcessors()
  case class WorkComplete(result: WorkResult)

  implicit val ec = context.dispatcher
  
  
  private var mandelbrotRenderer = new Renderer(context.system) with MandelbrotAlgorithm
  private var juliaRenderer = new Renderer(context.system) with JuliaAlgorithm

  override def preStart() = {
    //log.info(s"WORKER: sending WorkerCreated to: $master")
    master ! WorkerCreated(self)
  }

  private def processWork(work: Work) = {
    //log.info(s"WORKER: processing work: ${work.task}")
    work.task.renderParams.algorithmParams match {
      case _: MandelbrotParams => {
        val imageSegment = mandelbrotRenderer.render(NUM_THREADS, work.task)
        WorkResult(work, imageSegment)
      }
      case _: JuliaParams => {
        val imageSegment = juliaRenderer.render(NUM_THREADS, work.task)
        WorkResult(work, imageSegment)
      }
    }
  }

  private def doWork(work: Work): Unit = {
    Future {
      val result = processWork(work)
      WorkComplete(result)
    } pipeTo self
  }

  private def working(work: Work): Receive = {
    case WorkIsReady =>
    case NoWorkToBeDone =>
    case WorkToBeDone(_) =>
    case WorkComplete(result) =>
      //log.info(s"WORKER: WorkComplete: ${result.task}")
      master ! WorkIsDone(self, result)
      master ! WorkerRequestsWork(self)
      context.become(idle)
  }

  private def idle: Receive = {
    case WorkIsReady =>
      //log.info("WORKER: Requesting work.")
      master ! WorkerRequestsWork(self)
    case WorkToBeDone(work) =>
      //log.info(s"WORKER: Got work: ${work.task}")
      doWork(work)
      context.become(working(work))
    case NoWorkToBeDone =>
  }

  def receive = idle
}