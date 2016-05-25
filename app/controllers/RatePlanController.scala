package controllers
import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.memsub.{Digipack, Membership}
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Results._

class RatePlanController(googleAuthAction: GoogleAuthenticatedAction, membershipIds: MembershipRatePlanIds, digipackIds: DigitalPackRatePlanIds) {

  private def tupleToRatePlanMap(t: (String, String)) = {
    Map("ratePlanId" -> t._1, "ratePlanName" -> t._2)
  }

  def all = googleAuthAction {
    Ok(Json.obj(
      Membership.id -> Json.toJson(Seq(
        membershipIds.friend.get -> "Friend",
        membershipIds.supporterMonthly.get -> "Supporter monthly",
        membershipIds.supporterYearly.get -> "Supporter yearly",
        membershipIds.partnerMonthly.get -> "Partner monthly",
        membershipIds.partnerYearly.get -> "Partner yearly",
        membershipIds.patronMonthly.get -> "Patron monthly",
        membershipIds.patronYearly.get -> "Patron yearly"
      ).map(tupleToRatePlanMap)),
      Digipack.id -> Json.toJson(Seq(
        digipackIds.digitalPackMonthly.get -> "Digital pack monthly",
        digipackIds.digitalPackQuaterly.get -> "Digital pack quarterly",
        digipackIds.digitalPackYearly.get -> "Digital pack yearly"
      ).map(tupleToRatePlanMap))
    ))
  }
}
