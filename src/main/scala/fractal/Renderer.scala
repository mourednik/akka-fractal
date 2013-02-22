package fractal

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem

class Renderer(val actorSystem: ActorSystem) {
  this: RendererAlgorithm =>

  implicit val dispatcher = actorSystem.dispatcher

  private def renderSegment(task: SubTask) = computeImageSegment(task)

  def render(numThreads: Int, renderTask: Task): ImageSegment = {
    val subtasks = renderTask.makeSubTasks(numThreads)
    val segments = subtasks.map(subtask => Future(renderSegment(subtask)))
    val combinedSegments = Future.reduce(segments)(_ + _)
    Await.result(combinedSegments, 10 seconds)
  }
}

trait RendererAlgorithm {
  def computeImageSegment(parameters: SubTask): ImageSegment
}