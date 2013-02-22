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

  def render(task: Task, panel: GraphicsPanel) = {
    val future = master ? task
    future.onSuccess {
      case image: ImageSegment => panel.drawImage(image)
      case _ =>
    }
  }

  def shutDown {
    system.shutdown
    client.shutDown
  }
}

class DistributedRendererClient(system: ActorSystem) {

  implicit val timeout = Timeout(20 seconds)

  val worker = system.actorOf(Props[Worker])

  def shutDown {
    system.shutdown
  }
}
