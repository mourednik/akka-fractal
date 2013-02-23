package fractal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import akka.actor.AddressFromURIString
import akka.actor.Deploy
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory

class DistributedRenderer() {
  private val config = ConfigFactory.load
  private implicit val system = ActorSystem("MasterSystem", config.getConfig("masterSystem"))
  private implicit val timeout = Timeout(20 seconds)

  private val master = system.actorOf(Props[Master], "master")
  private val client = new DistributedRendererClient

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

  def shutdown {
    system.shutdown
    client.shutdown
  }
}

class DistributedRendererClient {
  private val config = ConfigFactory.load
  private implicit val system = ActorSystem("WorkerSystem", config.getConfig("workerSystem"))
  private val master = system.actorFor("akka://MasterSystem@10.59.100.85:2554/user/master")
  private implicit val timeout = Timeout(20 seconds)
  private val worker = system.actorOf(Props(new Worker(master)), "worker")

  def shutdown = system.shutdown
}
