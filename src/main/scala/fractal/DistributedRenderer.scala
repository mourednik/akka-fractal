package fractal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

class DistributedRenderer() {
  private val config = ConfigFactory.load
  private implicit val system = ActorSystem("MasterSystem", config.getConfig("masterSystem"))
  private val timeoutSeconds = system.settings.config.getString("settings.timeout-seconds").toInt
  private implicit val timeout = Timeout(timeoutSeconds seconds)

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
  private implicit val timeout = Timeout(20 seconds)
  
  private val masterIP = system.settings.config.getString("masterSystem.akka.remote.netty.hostname")
  private val masterPort = system.settings.config.getString("masterSystem.akka.remote.netty.port")
  
  private val master = system.actorFor(s"akka://MasterSystem@$masterIP:$masterPort/user/master") //   
  private val worker = system.actorOf(Props(new Worker(master)), "worker")

  def shutdown = system.shutdown
}
