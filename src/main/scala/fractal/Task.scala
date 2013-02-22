package fractal

import scala.collection.immutable

/**
 * Task
 */
case class Task(val renderParams: RenderParams, val id: Int) extends Serializable {

  def makeSubTasks(numSubTasks: Int): immutable.IndexedSeq[SubTask] = {
    val containerSubTask = SubTask(renderParams, (0, renderParams.dimension.y), 0, id)
    containerSubTask.makeSubTasks(numSubTasks)
  }

  override def hashCode: Int = {
    renderParams.hashCode + id.hashCode
  }

  override def equals(other: Any) = {
    other match {
      case that: Task => {
        this.renderParams == that.renderParams &&
          this.id == that.id
      }
      case _ => false
    }
  }

  override def toString = {
    s"$renderParams $id $hashCode"
  }
}

/**
 * SubTask
 */
class SubTask(renderParams: RenderParams, id: Int, val range: (Int, Int), val index: Int)
  extends Task(renderParams, id) {

  def getHeight = range._2 - range._1

  private def combineWith(other: SubTask) = {
    val lowerBound = Math.min(range._1, other.range._1)
    val upperBound = Math.max(range._2, other.range._2)
    SubTask(renderParams, (lowerBound, upperBound))
  }

  def +(other: SubTask) = {
    if (renderParams != other.renderParams) {
      sys.error("Tasks have different RenderParams.")
    } else if (range._2 == other.range._1 ||
      other.range._2 == range._1) {
      combineWith(other)
    } else {
      sys.error("Only adjacent segments can be combined.")
    }
  }

  private def makeSubTask(index: Int, height: Int, offset: Int) = {
    val lowerBound = index * height + offset
    val upperBound = (index + 1) * height
    val subRange = (lowerBound, upperBound)
    SubTask(renderParams, subRange, index, id)
  }

  private def makeLastSubTask(index: Int, height: Int, offset: Int, upperBound: Int) = {
    val lowerBound = index * height + offset
    val subRange = (lowerBound, upperBound)
    SubTask(renderParams, subRange, index, id)
  }

  override def makeSubTasks(numThreads: Int) = {
    val height = getHeight
    val subHeight = height / numThreads
    for (i <- 0 until numThreads) yield {
      if (i < numThreads - 1)
        makeSubTask(i, subHeight, range._1)
      else
        makeLastSubTask(i, subHeight, range._1, height)
    }
  }
}

object SubTask {

  private def makeSubTask(renderParams: RenderParams, id: Int, range: (Int, Int), index: Int) = {
    if (range._1 < 0 || range._2 > renderParams.dimension.y)
      sys.error("SegmentBounds exceed dimensions")
    else
      new SubTask(renderParams, id, range, index)
  }

  def apply(renderParams: RenderParams, range: (Int, Int), index: Int, id: Int) = {
    makeSubTask(renderParams, id, range, index)
  }

  def apply(renderParams: RenderParams, range: (Int, Int)) = {
    makeSubTask(renderParams, 0, range, 0)
  }
}
