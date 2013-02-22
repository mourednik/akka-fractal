package fractal

import org.scalatest.FunSpec
import org.scalatest.matchers.MustMatchers

class ConfigParserTest extends FunSpec with MustMatchers {

  val m1 = "name 0.0 0.0 0.5 mandelbrot 512"
  val j1 = "name 0.0 0.0 0.5 julia 512 -0.4 0.6"

  val m1instance = (Location("name", Coordinate(0.0, 0.0), 0.5), MandelbrotParams(512))
  val j1instance = (Location("name", Coordinate(0.0, 0.0), 0.5), JuliaParams(512, Complex(-0.4, 0.6)))

  describe("ConfigParser") {
    it("should parse Mandelbrot config") {
      ConfigParser.parse(m1) must equal(m1instance)
    }
    it("should parse Julia config") {
      ConfigParser.parse(j1) must equal(j1instance)
    }
  }
}