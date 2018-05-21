package controllers

import play.api.mvc.{AbstractController, ControllerComponents}

class HealthCheckController(components: ControllerComponents) extends AbstractController(components) {

  def healthCheck = Action {
    Ok("200 OK")
  }
}