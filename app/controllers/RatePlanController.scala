package controllers
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Results._

class RatePlanController(membershipIds: MembershipRatePlanIds, digipackIds: DigitalPackRatePlanIds) {

  def all = Action {
    Ok(Json.toJson(Map(
      membershipIds.friend.get -> "Friend",
      membershipIds.supporterMonthly.get -> "Supporter monthly",
      membershipIds.supporterYearly.get -> "Supporter yearly",
      membershipIds.partnerMonthly.get -> "Partner monthly",
      membershipIds.partnerYearly.get -> "Partner yearly",
      membershipIds.patronMonthly.get -> "Patron monthly",
      membershipIds.patronYearly.get -> "Patron yearly"
    ) ++
    Map(
      digipackIds.digitalPackMonthly.get -> "Digital pack monthly",
      digipackIds.digitalPackQuaterly.get -> "Digital pack quarterly",
      digipackIds.digitalPackYearly.get -> "Digital pack yearly"
    )))
  }
}
