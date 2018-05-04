package actions

import actions.GoogleAuthAction.GoogleAuthRequest
import com.gu.googleauth
import com.gu.memsub.auth.common.MemSub.Google._
import com.typesafe.config.Config
import controllers.routes
import play.api.libs.ws.WSClient
import play.api.mvc.ActionBuilder
import play.api.mvc.Security.AuthenticatedRequest

import scala.concurrent.ExecutionContext

class GoogleAuthAction(config: Config, ws: WSClient)(implicit ec: ExecutionContext) extends googleauth.Actions with googleauth.Filters {

  implicit def wsClient: WSClient = ws

  val authConfig = googleAuthConfigFor(config)
  val loginTarget = routes.AuthController.loginAction()
  val defaultRedirectTarget = routes.StaticController.index
  val failureRedirectTarget = routes.AuthController.loginAction()

  lazy val groupChecker = googleGroupCheckerFor(config)

  val GoogleAuthAction: ActionBuilder[GoogleAuthRequest] = AuthAction andThen requireGroup[GoogleAuthRequest](Set(
    "subscriptions-promotion-tool@guardian.co.uk"  // Managed by Reader Revenue Dev Managers.
  ))
}

object GoogleAuthAction {
  type GoogleAuthRequest[A] = AuthenticatedRequest[A, googleauth.UserIdentity]
  type GoogleAuthenticatedAction = ActionBuilder[GoogleAuthRequest]
}
