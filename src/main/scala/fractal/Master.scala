package fractal

import scala.collection.mutable

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Terminated
import akka.actor.actorRef2Scala

class Master extends Actor with ActorLogging {

  import MasterWorkerProtocol._

  var taskCounter = 0

  val workResultAggregator = new WorkResultCollector(this)
  val workers = mutable.Map.empty[ActorRef, Option[Work]]
  val workQ = mutable.Queue.empty[Work]

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
      //log.info(s"MASTER: Worker created: $worker")
      context.watch(worker)
      workers += (worker -> None)
      notifyWorkers()

    case WorkerRequestsWork(worker) =>
      //log.info(s"MASTER: Worker requests work: $worker")
      if (workers.contains(worker)) {
        if (workQ.isEmpty)
          worker ! NoWorkToBeDone
        else if (workers(worker) == None) {
          val work = workQ.dequeue()
          workers += (worker -> Some(work))
          //log.info(s"MASTER: sending work ${work.task}")
          worker ! WorkToBeDone(work)
        }
      }

    case WorkIsDone(worker, workResult) =>
      if (!workers.contains(worker))
        log.error(s"MASTER: Unknown worker $worker")
      else {
        //log.info(s"MASTER: Received workResult ${workResult.task}")
        workers += (worker -> None)
        workResultAggregator.insertResult(workResult)
      }

    case Terminated(worker) =>
      if (workers.contains(worker) && workers(worker) != None) {
        //log.error(s"MASTER: $worker died while processing ${workers(worker)}")
        val work = workers(worker).get
        workers -= worker
        workQ.enqueue(work)
        notifyWorkers
      }

    case renderParams: RenderParams => {
      val task = Task(renderParams, taskCounter)
      taskCounter += 1
      val size = renderParams.dimension.x * renderParams.dimension.y * 4
      val numSubTasks = math.max(1, size / 512000)
      val subtasks = task.makeSubTasks(numSubTasks)
      val thisSender = sender
      //log.info(s"MASTER: Received Task. Preparing collector for $task")
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