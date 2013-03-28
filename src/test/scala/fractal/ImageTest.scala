package fractal

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class ImageTest extends FunSuite with ShouldMatchers {

  val pixels1 = Array[Short](1, 2, 3, 4)
  val pixels2 = Array[Short](1, 2, 3, 4)
  val dimension = Dimension(2, 4)
  val location = Location("Another Mandelbrot", Coordinate(-0.1592, -1.0317), 32.0)
  val maxIterations = 512
  val mandelbrotParameters = MandelbrotParams(maxIterations)

  test("Adjacent segments should combine") {
    val task1 = SubTask(RenderParams(dimension, location, mandelbrotParameters), (0, 1))
    val task2 = SubTask(RenderParams(dimension, location, mandelbrotParameters), (1, 2))
    val imageSegment1 = new ImageSegment(pixels1, task1)
    val imageSegment2 = new ImageSegment(pixels2, task2)

    val combinedSegments = imageSegment1 + imageSegment2

    combinedSegments.lowerBound should equal(0)
    combinedSegments.upperBound should equal(2)
  }

  test("Non adjacent segments should not combine") {
    val task1 = SubTask(RenderParams(dimension, location, mandelbrotParameters), (0, 1))
    val task2 = SubTask(RenderParams(dimension, location, mandelbrotParameters), (2, 3))
    val imageSegment1 = new ImageSegment(pixels1, task1)
    val imageSegment2 = new ImageSegment(pixels2, task2)

    evaluating {
      imageSegment1 + imageSegment2
    } should produce[Exception]

  }
}
