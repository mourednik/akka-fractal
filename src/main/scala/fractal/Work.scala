package fractal

import scala.Array.canBuildFrom
import scala.collection.mutable

import akka.actor.ActorRef

/**
 * Work
 */
case class Work(val customer: ActorRef, val subtask: SubTask) extends Serializable

/**
 * WorkResult
 */
class WorkResult(customer: ActorRef, subtask: SubTask, val imageSegment: ImageSegment)
  extends Work(customer, subtask) {
  def getImageSegment = imageSegment
}

object WorkResult {
  def apply(work: Work, imageSegment: ImageSegment) =
    new WorkResult(work.customer, work.subtask, imageSegment)
}

/**
 * WorkResultAggregator
 */
class WorkResultAggregator(val master: Master) {
  private var resultMap = new mutable.HashMap[Task, WorkResultContainer]

  def prepareForCollection(task: Task, numSubTasks: Int) {
    resultMap += (task -> new WorkResultContainer(numSubTasks))
  }

  def insertResult(result: WorkResult) {
    val resultContainer = resultMap(result.subtask)
    resultContainer(result.subtask.index) = result

    if (resultContainer.isFull) {
      resultMap.remove(result.subtask)
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

  def apply(index: Int) = {
    array.apply(index)
  }

  def update(index: Int, result: WorkResult) {
    if (array(index) == null) {
      currentSize += 1
    }
    array.update(index, result)
  }

  def isFull = {
    size == currentSize
  }

  def getReducedResult = array.map(_.getImageSegment).reduce(_ + _)
}