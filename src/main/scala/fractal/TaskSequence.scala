package fractal

object TaskSequence {

  def makeSequence(startTask: Task, finalZoom: Double, numFrames: Int) = {
    if (numFrames < 2) {
      sys.error("numFrames < 2")
    }
    val dimension = startTask.renderParams.dimension
    val algorithmParameters = startTask.renderParams.algorithmParams
    val startLocation = startTask.renderParams.location
    val startZoom = startLocation.zoom
    val zoomIncrement = (finalZoom - startLocation.zoom) / numFrames

    val locations = for (i <- 0 to numFrames) yield {
      val zoom = startZoom + i * zoomIncrement
      Location(startLocation.name, startLocation.coordinate, zoom)
    }
    locations.map(location => Task(RenderParams(dimension, location, algorithmParameters)))
  }
}