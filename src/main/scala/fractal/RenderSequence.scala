package fractal

object RenderSequence {

  def makeSequence(startParams: RenderParams, finalZoom: Double, numFrames: Int) = {
    if (numFrames < 2) {
      sys.error("numFrames < 2")
    }
    val dimension = startParams.dimension
    val algorithmParameters = startParams.algorithmParams
    val startLocation = startParams.location
    val startZoom = startLocation.zoom
    val zoomIncrement = (finalZoom - startLocation.zoom) / numFrames

    val locations = for (i <- 0 to numFrames) yield {
      val zoom = startZoom + i * zoomIncrement
      Location(startLocation.name, startLocation.coordinate, zoom)
    }
    locations.map(location => RenderParams(dimension, location, algorithmParameters))
  }
}