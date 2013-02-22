package fractal

import scala.Array.canBuildFrom
import scala.collection.mutable

import akka.actor.ActorRef

/**
 * Work
 */
case class Work(val customer: ActorRef, val task: SubTask) extends Serializable

/**
 * WorkResult
 */
class WorkResult(customer: ActorRef, task: SubTask, val imageSegment: ImageSegment)
  extends Work(customer, task) {
  def getImageSegment = imageSegment
}

object WorkResult {
  def apply(work: Work, imageSegment: ImageSegment) =
    new WorkResult(work.customer, work.task, imageSegment)
}

/**
 * WorkResultCollector
 */
class WorkResultCollector(val master: Master) {
  private var resultMap = new mutable.HashMap[Task, WorkResultContainer]

  def prepareForCollection(task: Task, numSubTasks: Int) {
    resultMap += (task -> new WorkResultContainer(numSubTasks))
  }

  def insertResult(result: WorkResult) {
    val resultContainer = resultMap(result.task)
    resultContainer(result.task.index) = result

    if (resultContainer.isFull) {
      resultMap.remove(result.task)
      val reducedResult = resultContainer.getReducedResult
      master.returnResult(result.customer, reducedResult)
    }
  }
}

/**
 * WorkResultContainer
 */
class WorkResultContainer(size: Int) {
  private var currentSize = 0
  private val array = new Array[WorkResult](size)

  def apply(i: Int) = {
    array.apply(i)
  }

  def update(i: Int, x: WorkResult) {
    if (array(i) == null) {
      currentSize += 1
    }
    array.update(i, x)
  }

  def isFull = {
    size == currentSize
  }

  def getReducedResult = array.map(_.getImageSegment).reduce(_ + _)
}