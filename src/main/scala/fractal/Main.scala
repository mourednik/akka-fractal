package fractal

object Main extends App {

  override def main(args: Array[String]) {
    if (args.length == 1 && args(0) == "client") {
      val client = new DistributedRendererClient
      readLine("Client started. Press any key to exit.")
      client.shutdown()
    } else
      MainFrame.main(args)
  }
}
