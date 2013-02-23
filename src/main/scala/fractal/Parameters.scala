package fractal

class AlgorithmParams extends Serializable

case class MandelbrotParams(var maxIterations: Int) extends AlgorithmParams

case class JuliaParams(var maxIterations: Int, var coefficient: Complex) extends AlgorithmParams

case class Coordinate(x: Double, y: Double)

case class Location(val name: String, val coordinate: Coordinate, val zoom: Double) extends Serializable {

  override def equals(other: Any) = other match {
    case that: Location =>
      this.name == that.name &&
        this.coordinate == that.coordinate &&
        this.zoom == that.zoom
    case _ => false
  }
}

case class Dimension(val x: Int, val y: Int) extends Serializable {
  
  override def equals(other: Any) = other match {
    case that: Dimension => (this.x == that.x && this.y == that.y)
    case _ => false
  }
}

case class RenderParams(dimension: Dimension, location: Location, algorithmParams: AlgorithmParams)

object DefaultParameters {

  def dimension = Dimension(1280, 800)

  def iterations = 128

  def mandelbrotLocation = Location("Default", Coordinate(-0.25, 0.0), 0.5)

  def mandelbrotParameters = MandelbrotParams(iterations)

  def juliaLocation = Location("Default", Coordinate(0.0, 0.0), 0.5)

  def juliaParameters = JuliaParams(iterations, Complex(-0.4, 0.6))
}
