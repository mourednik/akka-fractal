package fractal

import scala.collection.mutable

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Terminated
import akka.actor.actorRef2Scala

class Master extends Actor with ActorLogging {

  import MasterWorkerProtocol._

  private val packetSize = context.system.settings.config.getString("settings.packet-size").toInt  
  private var taskCounter = 0

  private val workResultAggregator = new WorkResultAggregator(this)
  private val workers = mutable.Map.empty[ActorRef, Option[Work]]
  private val workQ = mutable.Queue.empty[Work]

  def returnResult(recipient: ActorRef, imageSegment: ImageSegment) {
    recipient ! imageSegment
  }

  def notifyWorkers(): Unit = {
    if (!workQ.isEmpty) {
      workers.foreach {
        case (worker, m) if (m.isEmpty) => worker ! WorkIsReady
        case _ =>
      }
    }
  }

  def receive = {
    case WorkerCreated(worker) =>
      context.watch(worker)
      workers += (worker -> None)
      notifyWorkers()

    case WorkerRequestsWork(worker) =>
      if (workers.contains(worker)) {
        if (workQ.isEmpty)
          worker ! NoWorkToBeDone
        else if (workers(worker) == None) {
          val work = workQ.dequeue()
          workers += (worker -> Some(work))
          worker ! WorkToBeDone(work)
        }
      }

    case WorkIsDone(worker, workResult) =>
      if (!workers.contains(worker))
        log.error(s"MASTER: Received workResult from unknown worker: $worker, $workResult")
      else {
        workers += (worker -> None)
        workResultAggregator.insertResult(workResult)
      }

    case Terminated(worker) =>
      if (workers.contains(worker) && workers(worker) != None) {
        val work = workers(worker).get
        workers -= worker
        workQ.enqueue(work)
        notifyWorkers
      }

    case renderParams: RenderParams => {
      val task = Task(renderParams, taskCounter)
      taskCounter += 1
      val imageSize = renderParams.dimension.x * renderParams.dimension.y * 4
      val numSubTasks = math.max(1, imageSize / packetSize)
      val subtasks = task.makeSubTasks(numSubTasks)
      val thisSender = sender
      workResultAggregator.prepareForCollection(task, numSubTasks)
      subtasks.foreach(subtask => workQ.enqueue(Work(sender, subtask)))
      notifyWorkers
    }
  }

}

object MasterWorkerProtocol extends Serializable {
  // Messages from Workers
  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)
  case class WorkIsDone(worker: ActorRef, workResult: WorkResult)

  // Messages to Workers
  case class WorkToBeDone(work: Work)
  case object WorkIsReady
  case object NoWorkToBeDone
}