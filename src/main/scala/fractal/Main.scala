package fractal

import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.util.Timeout

object Main extends App {

  override def main(args: Array[String]): Unit = {
    if (args.length == 1 && args(0) == "client") {
      implicit val timeout = Timeout(20 seconds)
      implicit val system = ActorSystem("FractalSystem")
      val client = new DistributedRendererClient(system)
      readLine("Client started. Press any key to exit.")
      system.shutdown
    } else {
      MainFrame.main(args)
    }
  }
}