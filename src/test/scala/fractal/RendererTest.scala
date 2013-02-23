package fractal

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import akka.actor.ActorSystem

class RendererTest extends FunSuite with ShouldMatchers {

  implicit val system = ActorSystem("FractalSystem")

  test("Mandelbrot algorithm") {
    val dimension = Dimension(640, 480)
    val numThreads = 8
    val renderTask = prepareMandelbrotTask(Dimension(640, 480))
    val renderer = new Renderer(system) with MandelbrotAlgorithm
    val image = renderer.render(numThreads, renderTask)
    image.getSize should equal(dimension.x * dimension.y)
    FileUtil.saveAsPNG("/tmp/mandelbrot.png", image.getBufferedImage)
  }

  test("Julia algorithm") {
    val dimension = Dimension(640, 480)
    val numThreads = 8
    val renderTask = prepareJuliaTask(dimension)
    val renderer = new Renderer(system) with JuliaAlgorithm
    val image = renderer.render(numThreads, renderTask)
    image.getSize should equal(dimension.x * dimension.y)
    FileUtil.saveAsPNG("/tmp/julia.png", image.getBufferedImage)
  }

  test("Handles unequally sized segments.") {
    val dimension = Dimension(640, 480)
    val numThreads = 7
    val renderTask = prepareJuliaTask(dimension)
    val renderer = new Renderer(system) with JuliaAlgorithm
    val image = renderer.render(numThreads, renderTask)
    image.getSize should equal(dimension.x * dimension.y)
  }  

  def prepareMandelbrotTask(dimension: Dimension) = {
    //val location = Location("Another Mandelbrot", Coordinate(-0.1592, -1.0317), 32.0);
    val location = Location("Default", Coordinate(-0.25, 0.0), 0.5);
    val mandelbrotParameters = MandelbrotParams(128)
    Task(RenderParams(dimension, location, mandelbrotParameters), 0)
  }

  def prepareJuliaTask(dimension: Dimension) = {
    val location = Location("Default", Coordinate(0.0, 0.0), 0.5);
    val parameters = JuliaParams(128, Complex(-0.4, 0.6))
    Task(RenderParams(dimension, location, parameters), 0)
  }

}