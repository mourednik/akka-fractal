package fractal

object ConfigParser {

  def parse(input: String): (Location, AlgorithmParams) = {
    val wordGrp = """(\w+)"""
    val realGrp = """(-?\d+.\d+)"""
    val intGrp = """(\d+)"""
    val anyGrp = """(.*)"""

    val initialPattern = s"$wordGrp $realGrp $realGrp $realGrp $wordGrp $anyGrp".r
    val mandelPattern = intGrp.r
    val juliaPattern = s"$intGrp $realGrp $realGrp".r

    val initialPattern(name, x, y, zoom, algorithm, paramString) = input
    val location = Location(name, Coordinate(x.toDouble, y.toDouble), zoom.toDouble)

    algorithm match {
      case "mandelbrot" =>
        val mandelPattern(maxIterations) = paramString
        val parameters = MandelbrotParams(maxIterations.toInt)
        (location, parameters)
      case "julia"      =>
        val juliaPattern(maxIterations, real, image) = paramString
        val parameters = JuliaParams(maxIterations.toInt, Complex(real.toDouble, image.toDouble))
        (location, parameters)
    }
  }
}
