package fractal

case class Complex(var real: Double, var image: Double) {

  def +(that: Complex) = new Complex(real + that.real, image + that.image)

  def *(that: Complex) = new Complex(
    real * that.real - image * that.image,
    real * that.image + that.real * image)

  def abs = Math.sqrt(real * real + image * image)

  def *=(that: Complex) = {
    val newReal = real * that.real - image * that.image
    image = real * that.image + that.real * image
    real = newReal
    this
  }

  def +=(that: Complex) = {
    real += that.real
    image += that.image
    this
  }
}

