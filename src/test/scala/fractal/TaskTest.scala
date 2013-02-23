package fractal

import org.scalatest.FunSpec
import org.scalatest.matchers.MustMatchers

class TaskTest extends FunSpec with MustMatchers {
  describe("Task") {
    it("should make subtasks.") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(2)
      subtasks.length must be(2)
    }

    it("should make subtasks with correct range specs.") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(2)
      subtasks(0).segmentLB must be(0)
      subtasks(0).segmentUB must be(height / 2)
      subtasks(1).segmentLB must be(height / 2)
      subtasks(1).segmentUB must be(height)
    }
  }

  describe("SubTask") {
    it("should make subtasks") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(2)
      val subsubtasks = subtasks(0).makeSubTasks(2)
    }

    it("should make subtasks with correct range specs.") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(2)
      val subsubtasks = subtasks(0).makeSubTasks(2)
      subsubtasks(0).segmentLB must be(0)
      subsubtasks(0).segmentUB must be(height / 4)
      subsubtasks(1).segmentLB must be(height / 4)
      subsubtasks(1).segmentUB must be(height / 2)
    }

    it("+ operator should combine subtasks") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(2)
      val combined = subtasks(0) + subtasks(1)
      combined.segmentLB must be(0)
      combined.segmentUB must be(height)
    }

    it("+ operator should be associative") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(2)
      val combined = subtasks(1) + subtasks(0)
      combined.segmentLB must be(0)
      combined.segmentUB must be(height)
    }

    it("+ operator should only accept adjacent segments") {
      val width = 640
      val height = 480
      val task = new Task(renderParams(Dimension(width, height)), 0)
      val subtasks = task.makeSubTasks(4)
      evaluating {
        val combined = subtasks(0) + subtasks(2)
      } must produce[Exception]
    }
  }

  def renderParams(dimension: Dimension) = {
    val location = DefaultParameters.mandelbrotLocation
    val mandelbrotParameters = DefaultParameters.mandelbrotParameters
    RenderParams(dimension, location, mandelbrotParameters)
  }
} 

