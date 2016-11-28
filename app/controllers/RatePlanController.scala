package controllers
import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.memsub.promo.CampaignGroup.{DigitalPack, GuardianWeekly, Membership, Newspaper}
import com.gu.memsub.Subscription.ProductRatePlanId
import conf.{PaperProducts, WeeklyPlans}
import play.api.libs.json._
import play.api.mvc.Results._

class RatePlanController(
  googleAuthAction: GoogleAuthenticatedAction,
  paperPlans: PaperProducts,
  membershipIds: MembershipRatePlanIds,
  digipackIds: DigitalPackRatePlanIds,
  weeklyPlans: WeeklyPlans
                        ) {

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
      DigitalPack.id -> Json.toJson(Seq(
        digipackIds.digitalPackMonthly -> "Digital pack monthly",
        digipackIds.digitalPackQuaterly -> "Digital pack quarterly",
        digipackIds.digitalPackYearly -> "Digital pack yearly"
      ).map(tupleToRatePlanMap)),
      Newspaper.id -> Json.toJson(Seq(
        paperPlans.delivery.sundayplus -> "Home Delivery Sunday+",
        paperPlans.delivery.weekend -> "Home Delivery Weekend",
        paperPlans.delivery.weekendplus -> "Home Delivery Weekend+",
        paperPlans.delivery.sixday -> "Home Delivery Sixday",
        paperPlans.delivery.sixdayplus -> "Home Delivery Sixday+",
        paperPlans.delivery.everyday -> "Home Delivery Everyday",
        paperPlans.delivery.everydayplus -> "Home Delivery Everyday+",
        paperPlans.voucher.sundayplus -> "Voucher Sunday+",
        paperPlans.voucher.weekend -> "Voucher Weekend",
        paperPlans.voucher.weekendplus -> "Voucher Weekend+",
        paperPlans.voucher.sixday -> "Voucher Sixday",
        paperPlans.voucher.sixdayplus -> "Voucher Sixday+",
        paperPlans.voucher.everyday -> "Voucher Everyday",
        paperPlans.voucher.everydayplus -> "Voucher Everyday+"
      ).map(tupleToRatePlanMap)),
      // TODO - Guardian Weekly - One Year
      GuardianWeekly.id -> Json.toJson(Seq(
        weeklyPlans.zoneA.yearly -> "Weekly Zone A Yearly",
          weeklyPlans.zoneA.quarterly -> "Weekly Zone A Quarterly",
        weeklyPlans.zoneB.yearly -> "Weekly Zone B Yearly",
        weeklyPlans.zoneB.quarterly -> "Weekly Zone B Quarterly"
      ).map(tupleToRatePlanMap))
    ))
  }
}
