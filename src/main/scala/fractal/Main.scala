package fractal

import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.util.Timeout

object Main extends App {

  override def main(args: Array[String]): Unit = {
    if (args.length == 1 && args(0) == "client") {
      val client = new DistributedRendererClient
      readLine("Client started. Press any key to exit.")
      client.shutdown
    } else {
      MainFrame.main(args)
    }
  }
}