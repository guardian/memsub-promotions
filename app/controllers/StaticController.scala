package controllers
import actions.GoogleAuthAction
import actions.GoogleAuthAction._
import play.api.mvc._

class StaticController(googleAuthAction: GoogleAuthenticatedAction) extends Controller {

  def index = googleAuthAction {
    Ok(views.html.index("Your new application is ready."))
  }
}
