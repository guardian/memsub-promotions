package actions

import com.gu.googleauth
import play.api.mvc.{ActionBuilder, AnyContent}
import play.api.mvc.Security.AuthenticatedRequest

object GoogleAuthAction {
  type GoogleAuthRequest[A] = AuthenticatedRequest[A, googleauth.UserIdentity]
  type GoogleAuthenticatedAction = ActionBuilder[GoogleAuthRequest, AnyContent]
}
