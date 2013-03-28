package fractal

import scala.annotation.tailrec

trait MandelbrotAlgorithm extends RendererAlgorithm {

  override def computeImageSegment(subtask: SubTask) = {

    import subtask.renderParams._

    val width = dimension.x
    val height = dimension.y
    val lowerBound = subtask.segmentLB
    val upperBound = subtask.segmentUB
    val segmentHeight = subtask.height
    val xPos = location.coordinate.x
    val yPos = location.coordinate.y

    val aspectRatio = width / height.toDouble
    val scale = 1.0 / location.zoom
    val aspectScale = aspectRatio * scale
    val xConst = xPos - (aspectRatio * scale * 0.5)
    val yConst = yPos - (scale * 0.5)

    val pixels = new Array[Short](width * segmentHeight)
    var bufferIndex = 0

    val parameters = algorithmParams.asInstanceOf[MandelbrotParams]
    val maxIterations = parameters.maxIterations

    for {
      yPixel <- lowerBound until upperBound
      xPixel <- 0 until width
    } {
      val xFrac = xPixel / width.toDouble
      val yFrac = yPixel / height.toDouble
      val xReal = aspectScale * xFrac + xPos + xConst
      val yReal = scale * yFrac + yPos + yConst

      var z = Complex(0, 0)
      var c = Complex(xReal, yReal)
      var i = 0

      do {
        z *= z
        z += c
        i += 1
      } while (z.abs < 2 && i < maxIterations)

      if (z.abs < 2)
        pixels(bufferIndex) = 0.toShort
      else
        pixels(bufferIndex) = (i * 256.0 / maxIterations).toShort
      bufferIndex += 1
    }
    new ImageSegment(pixels, subtask)
  }

}

trait JuliaAlgorithm extends RendererAlgorithm {

  override def computeImageSegment(subtask: SubTask) = {

    import subtask.renderParams._

    val width = dimension.x
    val height = dimension.y
    val lowerBound = subtask.segmentLB
    val upperBound = subtask.segmentUB
    val segmentHeight = subtask.height
    val xPos = location.coordinate.x
    val yPos = location.coordinate.y

    val aspectRatio = width / height.toDouble
    val scale = 1.0 / location.zoom
    val aspectScale = aspectRatio * scale
    val xConst = xPos - (aspectRatio * scale * 0.5)
    val yConst = yPos - (scale * 0.5)
    val pixels = new Array[Short](width * segmentHeight)
    var bufferIndex = 0

    val parameters = algorithmParams.asInstanceOf[JuliaParams]
    val maxIterations = parameters.maxIterations
    val coefficient = parameters.coefficient

    for {
      yPixel <- lowerBound until upperBound
      xPixel <- 0 until width
    } {
      val xFrac = xPixel / width.toDouble
      val yFrac = yPixel / height.toDouble
      val xReal = aspectScale * xFrac + xPos + xConst
      val yReal = scale * yFrac + yPos + yConst

      var result = 0 // set by the following function

      @tailrec
      def mainloop(i: Int, x: Double, y: Double) {
        if (i < maxIterations) {
          val xsquared = x * x
          val ysquared = y * y
          if (xsquared + ysquared < 4) {
            val xnext = xsquared - ysquared + coefficient.real
            val ynext = 2 * x * y + coefficient.image
            result += 1
            mainloop(i + 1, xnext, ynext)
          }
        }
      }
      mainloop(0, xReal, yReal)
      pixels(bufferIndex) = (result * 256.0 / maxIterations).toShort
      bufferIndex += 1
    }
    new ImageSegment(pixels, subtask)
  }
}


