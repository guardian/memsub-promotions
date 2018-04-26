package controllers
import com.gu.googleauth.{GoogleAuth, UserIdentity}
import com.gu.memsub.auth.common.MemSub.Google._
import com.typesafe.config.Config
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthController(config: Config, implicit val ws: WSClient) extends Controller {

  val googleAuthConfig = googleAuthConfigFor(config)
  val ANTI_FORGERY_KEY = "antiForgeryToken"

  /**
    * Redirect to Google with anti forgery token (that we keep in session storage - note that flashing is NOT secure)
    */
  def loginAction = Action.async { implicit request =>
    val antiForgeryToken = GoogleAuth.generateAntiForgeryToken()
    GoogleAuth.redirectToGoogle(googleAuthConfig, antiForgeryToken)
      .map(_.withSession(request.session + (ANTI_FORGERY_KEY -> antiForgeryToken)))
  }

  /**
    * User comes back from Google.
    * We must ensure we have the anti forgery token from the loginAction call and pass this into a verification call which
    * will return a Future[UserIdentity] if the authentication is successful. If unsuccessful then the Future will fail.
    */
  def oauth2Callback = Action.async { implicit request =>
    val session = request.session
    session.get(ANTI_FORGERY_KEY) match {
      case None =>
        Future.successful(Unauthorized("No anti forgery token"))
      case Some(token) =>
        GoogleAuth.validatedUserIdentity(googleAuthConfig, token).map { identity =>
          val redirect = Redirect(routes.StaticController.index())
          redirect.withSession { session + (UserIdentity.KEY -> Json.toJson(identity).toString) - ANTI_FORGERY_KEY }
        } recover { case e => Unauthorized(e.toString) }
    }
  }
}
