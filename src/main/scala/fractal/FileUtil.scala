package fractal

import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import javax.imageio.ImageIO

object FileUtil {

  def writeFile(filename: String, content: Array[Byte]) {
    val output = new FileOutputStream(filename)
    output.write(content)
    output.close
  }

  def saveAsPNG(filename: String, image: BufferedImage) {
    try {
      var outputfile = new File(filename);
      ImageIO.write(image, "png", outputfile);
    } catch {
      case e: IOException =>
    }
  }
}