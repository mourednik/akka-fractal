package fractal

import java.awt.image.BufferedImage

import scala.Array.canBuildFrom

class Image(pixels: Array[Short], task: Task) extends Serializable {

  def getSize = pixels.length

  def getBufferedImage: BufferedImage = {
    import task.renderParams._
    val image = new BufferedImage(dimension.x, dimension.y, BufferedImage.TYPE_INT_RGB)
    for (x <- 0 until dimension.x; y <- 0 until dimension.y) {
      val pixval = pixels(y * dimension.x + x)
      image.setRGB(x, y, pixels(y * dimension.x + x))
    }
    image
  }
}

case class ImageSegment(protected val pixels: Array[Short], protected val subtask: SubTask)
  extends Image(pixels, subtask) {

  def lowerBound = subtask.segmentLB

  def upperBound = subtask.segmentUB

  def getRenderTask = subtask

  def +(other: ImageSegment) = {
    def combinedTask = subtask + other.subtask
    val combinedPixels = pixels ++ other.pixels
    new ImageSegment(combinedPixels, combinedTask)
  }
}
