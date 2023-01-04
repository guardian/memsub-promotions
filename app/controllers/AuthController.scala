package controllers
import com.gu.googleauth
import com.gu.googleauth.GoogleGroupChecker
import com.gu.memsub.auth.common.MemSub.Google._
import com.typesafe.config.Config
import play.api.http.HttpConfiguration
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

class AuthController(val wsClient: WSClient, components: ControllerComponents, config: Config, httpConfiguration: HttpConfiguration, val ws: WSClient)
  extends AbstractController(components) with googleauth.LoginSupport with googleauth.Filters {

  override val authConfig = googleAuthConfigFor(config, httpConfiguration)
  override lazy val groupChecker: GoogleGroupChecker = googleGroupCheckerFor(config)

  val ANTI_FORGERY_KEY = "antiForgeryToken"

  /**
    * Redirect to Google with anti forgery token (that we keep in session storage - note that flashing is NOT secure)
    */
  def loginAction: Action[AnyContent] = Action.async { implicit request =>
    startGoogleLogin()
  }

  /**
    * User comes back from Google.
    * We must ensure we have the anti forgery token from the loginAction call and pass this into a verification call which
    * will return a Future[UserIdentity] if the authentication is successful. If unsuccessful then the Future will fail.
    */

  def oauth2Callback: Action[AnyContent] = Action.async { implicit request =>
    processOauth2Callback(Set(
      "subscriptions-promotion-tool@guardian.co.uk" // Managed by Reader Revenue Dev Managers.
    ), groupChecker)
  }

  override val failureRedirectTarget: Call = routes.AuthController.loginAction
  override val defaultRedirectTarget: Call = routes.StaticController.index
}
