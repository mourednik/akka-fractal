package fractal

case class Complex(real: Double, image: Double) {

  def +(that: Complex) = Complex(real + that.real, image + that.image)

  def *(that: Complex) = Complex(
    real * that.real - image * that.image,
    real * that.image + that.real * image)

  def abs = Math.sqrt(real * real + image * image)
}

