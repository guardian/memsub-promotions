package actions

import actions.GoogleAuthAction.GoogleAuthRequest
import com.gu.googleauth
import com.gu.memsub.auth.common.MemSub.Google._
import com.typesafe.config.Config
import controllers.routes
import play.api.mvc.ActionBuilder
import play.api.mvc.Security.AuthenticatedRequest

import scala.concurrent.ExecutionContext

class GoogleAuthAction(config: Config)(implicit ec: ExecutionContext) extends googleauth.Actions with googleauth.Filters {

  val authConfig = googleAuthConfigFor(config)
  val loginTarget = routes.AuthController.loginAction()
  lazy val groupChecker = googleGroupCheckerFor(config)

  val GoogleAuthAction: ActionBuilder[GoogleAuthRequest] = AuthAction andThen requireGroup[GoogleAuthRequest](Set(
    "directteam@guardian.co.uk",
    "subscriptions.dev@guardian.co.uk",
    "memsubs.dev@guardian.co.uk",
    "membership.wildebeest@guardian.co.uk",
    "identitydev@guardian.co.uk",
    "touchpoint@guardian.co.uk",
    "crm@guardian.co.uk",
    "membership.testusers@guardian.co.uk"
  ))
}

object GoogleAuthAction {
  type GoogleAuthRequest[A] = AuthenticatedRequest[A, googleauth.UserIdentity]
  type GoogleAuthenticatedAction = ActionBuilder[GoogleAuthRequest]
}
