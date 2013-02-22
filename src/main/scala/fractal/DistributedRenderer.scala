package fractal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

class DistributedRenderer() {
  implicit val timeout = Timeout(20 seconds)
  implicit val system = ActorSystem("FractalSystem")

  private val master = system.actorOf(Props[Master], name = "master")
  private val client = new DistributedRendererClient(system)

  def renderToPanel(renderParams: RenderParams, panel: GraphicsPanel) = {
    val future = master ? renderParams
    future.onSuccess {
      case image: Image => panel.drawImage(image.getBufferedImage)
      case _ =>
    }
  }

  def renderToFile(renderParams: RenderParams, filename: String) {
    val future = master ? renderParams
    future.onSuccess {
      case image: Image => FileUtil.saveAsPNG(filename, image.getBufferedImage)
      case _ =>
    }
  }

  def shutDown {
    system.shutdown
  }
}

class DistributedRendererClient(system: ActorSystem) {
  implicit val timeout = Timeout(20 seconds)
  val worker = system.actorOf(Props[Worker])
}
