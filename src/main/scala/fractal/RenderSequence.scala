package fractal

object RenderSequence {

  def makeSequence(start: RenderParams, endZoom: Double, numFrames: Int) = {
    if (numFrames < 2) {
      sys.error("numFrames < 2")
    }
    val dimension = start.dimension
    val algorithmParameters = start.algorithmParams
    val startLocation = start.location
    val startZoom = startLocation.zoom
    val zoomIncrement = (endZoom - startLocation.zoom) / numFrames

    val locations = for (i <- 0 to numFrames) yield {
      val zoom = startZoom + i * zoomIncrement
      Location(startLocation.name, startLocation.coordinate, zoom)
    }
    locations.map(location => RenderParams(dimension, location, algorithmParameters))
  }
}