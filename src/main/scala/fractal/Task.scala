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
class SubTask(renderParams: RenderParams, id: Int, val segmentRange: (Int, Int), val index: Int)
  extends Task(renderParams, id) {

  def getHeight = segmentRange._2 - segmentRange._1

  private def combineWith(other: SubTask) = {
    val lowerBound = Math.min(segmentRange._1, other.segmentRange._1)
    val upperBound = Math.max(segmentRange._2, other.segmentRange._2)
    SubTask(renderParams, (lowerBound, upperBound))
  }

  def +(other: SubTask) = {
    if (renderParams != other.renderParams) {
      sys.error("Tasks have different RenderParams.")
    } else if (segmentRange._2 == other.segmentRange._1 ||
      other.segmentRange._2 == segmentRange._1) {
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
        makeSubTask(i, subHeight, segmentRange._1)
      else
        makeLastSubTask(i, subHeight, segmentRange._1, height)
    }
  }
}

object SubTask {

  private def makeSubTask(renderParams: RenderParams, id: Int, segmentRange: (Int, Int), index: Int) = {
    if (segmentRange._1 < 0 || segmentRange._2 > renderParams.dimension.y)
      sys.error("SegmentBounds exceed dimensions")
    else
      new SubTask(renderParams, id, segmentRange, index)
  }

  def apply(renderParams: RenderParams, segmentRange: (Int, Int), index: Int, id: Int) = {
    makeSubTask(renderParams, id, segmentRange, index)
  }

  def apply(renderParams: RenderParams, segmentRange: (Int, Int)) = {
    makeSubTask(renderParams, 0, segmentRange, 0)
  }
}
