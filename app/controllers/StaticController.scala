package controllers
import actions.GoogleAuthAction
import actions.GoogleAuthAction._
import play.api.mvc._

class StaticController(googleAuthAction: GoogleAuthenticatedAction, components: ControllerComponents) extends AbstractController(components) {

  def index = googleAuthAction {
    Ok(views.html.index("Your new application is ready."))
  }
}
