package actions

import actions.GoogleAuthAction.GoogleAuthRequest
import com.gu.googleauth
import com.gu.memsub.auth.common.MemSub.Google._
import com.typesafe.config.Config
import controllers.routes
import play.api.libs.ws.WSClient
import play.api.mvc.{ActionBuilder, Call}
import play.api.mvc.Security.AuthenticatedRequest
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.ExecutionContext

class GoogleAuthAction(config: Config)(implicit ec: ExecutionContext, materializer: akka.stream.Materializer) extends googleauth.Actions with googleauth.Filters {

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
    "membership.testusers@guardian.co.uk",
    "acquisition@guardian.co.uk"
  ))

  override implicit def wsClient: WSClient = AhcWSClient()

  /**
    * The target that should be redirected to if login fails
    */
  override val failureRedirectTarget: Call = Call("", "")

  /**
    * The target that should be redirected to if no redirect URL is provided (generally `home`)
    */
  override val defaultRedirectTarget: Call = Call("", "")
}

object GoogleAuthAction {
  type GoogleAuthRequest[A] = AuthenticatedRequest[A, googleauth.UserIdentity]
  type GoogleAuthenticatedAction = ActionBuilder[GoogleAuthRequest]
}
