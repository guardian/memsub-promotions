package controllers
import actions.GoogleAuthAction
import actions.GoogleAuthAction._
import play.api.mvc._
import wiring.AppComponents.Stage

class StaticController(googleAuthAction: GoogleAuthenticatedAction, components: ControllerComponents, stage: Stage) extends AbstractController(components) {

  def index = googleAuthAction {
    Ok(views.html.index("Your new application is ready.", stage))
  }
}
