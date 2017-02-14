package controllers

import play.api.mvc.{Action, Controller}

class HealthCheckController extends Controller {

  def healthCheck = Action {
    Ok("200 OK") //todo: not okay  (╯°□°）╯︵ ┻━┻
  }
}