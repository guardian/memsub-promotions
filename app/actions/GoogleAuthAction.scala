package actions

import actions.GoogleAuthAction.GoogleAuthRequest
import com.gu.googleauth
import com.gu.memsub.auth.common.MemSub.Google._
import com.typesafe.config.Config
import controllers.routes
import play.api.mvc.ActionBuilder
import play.api.mvc.Security.AuthenticatedRequest

class GoogleAuthAction(config: Config) extends googleauth.Actions with googleauth.Filters {


  val authConfig = googleAuthConfigFor(config)

  val loginTarget = routes.AuthController.loginAction()

  lazy val groupChecker = googleGroupCheckerFor(config)

  val GoogleAuthAction: ActionBuilder[GoogleAuthRequest] = AuthAction

}

object GoogleAuthAction {
  type GoogleAuthRequest[A] = AuthenticatedRequest[A, googleauth.UserIdentity]
  type GoogleAuthenticatedAction = ActionBuilder[GoogleAuthRequest]
}
