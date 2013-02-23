package fractal

class Navigator(distRenderer: DistributedRenderer) {

  private var panel: Option[GraphicsPanel] = None
  private var dimension = DefaultParameters.dimension
  private var location = DefaultParameters.mandelbrotLocation
  private var algparams: AlgorithmParams = DefaultParameters.mandelbrotParameters

  def loadJulia {
    location = DefaultParameters.juliaLocation
    algparams = DefaultParameters.juliaParameters
    requestRenderToPanel
  }
  
  def loadMandelbrot {
    location = DefaultParameters.mandelbrotLocation
    algparams = DefaultParameters.mandelbrotParameters
    requestRenderToPanel
  }
  
  def setGraphicsPanel(panel: GraphicsPanel) {
    this.panel = Some(panel)
  }

  def setDimension(newDimension: Dimension) {
    dimension = Dimension(newDimension.x, newDimension.y)
  }

  def incrementZoom(increment: Double) {
    val newZoom = location.zoom * increment
    location = Location(location.name, location.coordinate, newZoom)
    requestRenderToPanel
  }

  def incrementXCoordinate(increment: Double) {
    val scaledIncrement = increment / location.zoom
    val newCoordinate = Coordinate(location.coordinate.x + scaledIncrement, location.coordinate.y)
    location = Location(location.name, newCoordinate, location.zoom)
    requestRenderToPanel
  }

  def incrementYCoordinate(increment: Double) {
    val scaledIncrement = increment / location.zoom
    val newCoordinate = Coordinate(location.coordinate.x, location.coordinate.y + scaledIncrement)
    location = Location(location.name, newCoordinate, location.zoom)
    requestRenderToPanel
  }

  def incrementIterations(increment: Int) {
    algparams match {
      case mandelParams: MandelbrotParams =>
        val iterations = Math.max(0, mandelParams.maxIterations + increment)
        algparams = MandelbrotParams(iterations)
      case juliaParams: JuliaParams =>
        val iterations = Math.max(0, juliaParams.maxIterations + increment)
        algparams = JuliaParams(iterations, juliaParams.coefficient)
    }
    requestRenderToPanel
  }

  def requestRenderToPanel {
    if (panel.isDefined)
      distRenderer.renderToPanel(RenderParams(dimension, location, algparams), panel.get)
  }

  def getParamString = {
    s"$dimension, $location, $algparams"
  }
}