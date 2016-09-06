package controllers
import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.memsub.Subscription.ProductRatePlanId
import com.gu.memsub.{Digipack, Membership}
import conf.PaperProducts
import play.api.libs.json._
import play.api.mvc.Results._

class RatePlanController(
  googleAuthAction: GoogleAuthenticatedAction,
  paperPlans: PaperProducts,
  membershipIds: MembershipRatePlanIds,
  digipackIds: DigitalPackRatePlanIds) {

  private def tupleToRatePlanMap(t: (ProductRatePlanId, String)) = {
    Map("ratePlanId" -> t._1.get, "ratePlanName" -> t._2)
  }

  def all = googleAuthAction {
    Ok(Json.obj(
      Membership.id -> Json.toJson(Seq(
        membershipIds.friend -> "Friend",
        membershipIds.supporterMonthly -> "Supporter monthly",
        membershipIds.supporterYearly -> "Supporter yearly",
        membershipIds.partnerMonthly -> "Partner monthly",
        membershipIds.partnerYearly -> "Partner yearly",
        membershipIds.patronMonthly -> "Patron monthly",
        membershipIds.patronYearly -> "Patron yearly"
      ).map(tupleToRatePlanMap)),
      "digitalpack" -> Json.toJson(Seq(
        digipackIds.digitalPackMonthly -> "Digital pack monthly",
        digipackIds.digitalPackQuaterly -> "Digital pack quarterly",
        digipackIds.digitalPackYearly -> "Digital pack yearly",
        paperPlans.delivery.weekend -> "Paper delivery weekend",
        paperPlans.delivery.weekendplus -> "Paper delivery weekend+",
        paperPlans.delivery.sixday -> "Paper delivery sixday",
        paperPlans.delivery.sixdayplus -> "Paper delivery sixday+",
        paperPlans.delivery.everyday -> "Paper delivery everyday",
        paperPlans.delivery.everydayplus -> "Paper delivery everyday+",
        paperPlans.voucher.weekend -> "Paper voucher weekend",
        paperPlans.voucher.weekendplus -> "Paper voucher weekend+",
        paperPlans.voucher.sixday -> "Paper voucher sixday",
        paperPlans.voucher.sixdayplus -> "Paper voucher sixday+",
        paperPlans.voucher.everyday -> "Paper voucher everyday",
        paperPlans.voucher.everydayplus -> "Paper voucher everyday+"
      ).map(tupleToRatePlanMap))
    ))
  }
}
