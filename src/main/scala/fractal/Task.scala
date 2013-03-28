package fractal


/**
 * Task
 */
case class Task(renderParams: RenderParams, id: Int) extends Serializable {

  def height = renderParams.dimension.y

  def segmentLB = 0

  def segmentUB = height

  private def makeSubTask(index: Int, height: Int, offset: Int) = {
    val lb = index * height + offset
    val ub = (index + 1) * height + offset
    val segmentRange = (lb, ub)
    SubTask(renderParams, segmentRange, index, id)
  }

  private def makeLastSubTask(index: Int, height: Int, offset: Int) = {
    val lb = index * height + offset
    val segmentRange = (lb, segmentUB)
    SubTask(renderParams, segmentRange, index, id)
  }

  def makeSubTasks(numSubTasks: Int) = {
    val subTaskHeight = height / numSubTasks
    for (i <- 0 until numSubTasks) yield {
      if (i < numSubTasks - 1)
        makeSubTask(i, subTaskHeight, segmentLB)
      else
        makeLastSubTask(i, subTaskHeight, segmentLB)
    }
  }

  override def hashCode = renderParams.hashCode + id.hashCode

  override def equals(other: Any) =
    other match {
      case that: Task => {
        this.renderParams == that.renderParams &&
          this.id == that.id
      }
      case _          => false
    }

  override def toString = s"$renderParams $id $hashCode"
}

/**
 * SubTask
 */
class SubTask(renderParams: RenderParams, id: Int, private val segmentRange: (Int, Int), val index: Int)

  extends Task(renderParams, id) {

  override def height = segmentRange._2 - segmentRange._1

  override def segmentLB = segmentRange._1

  override def segmentUB = segmentRange._2

  private def combineWith(other: SubTask) = {
    val lb = Math.min(segmentLB, other.segmentLB)
    val ub = Math.max(segmentUB, other.segmentUB)
    SubTask(renderParams, (lb, ub))
  }

  def +(other: SubTask) =
    if (renderParams != other.renderParams) {
      sys.error("Tasks have different RenderParams.")
    } else if (segmentUB == other.segmentLB ||
      other.segmentUB == segmentLB) {
      combineWith(other)
    } else {
      sys.error("Only adjacent segments can be combined.")
    }

  override def toString = super.toString + s" $segmentRange $index"
}

object SubTask {

  private def makeSubTask(renderParams: RenderParams, id: Int, segmentRange: (Int, Int), index: Int) =
    if (segmentRange._1 < 0 || segmentRange._2 > renderParams.dimension.y)
      sys.error("SegmentBounds exceed dimensions")
    else
      new SubTask(renderParams, id, segmentRange, index)

  def apply(renderParams: RenderParams, segmentRange: (Int, Int), index: Int, id: Int) =
    makeSubTask(renderParams, id, segmentRange, index)

  def apply(renderParams: RenderParams, segmentRange: (Int, Int)) =
    makeSubTask(renderParams, 0, segmentRange, 0)
}
