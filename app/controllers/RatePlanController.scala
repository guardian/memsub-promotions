package controllers

import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.memsub.Subscription.ProductRatePlanId
import com.gu.memsub.promo.CampaignGroup.{DigitalPack, GuardianWeekly, Membership, Newspaper}
import com.gu.memsub.subsv2.services.CatalogService
import conf.{PaperProducts, WeeklyPlans}
import play.api.libs.json._
import play.api.mvc.Results._

import scala.concurrent.Future

class RatePlanController(

                          googleAuthAction: GoogleAuthenticatedAction,
                          paperPlans: PaperProducts,
                          membershipIds: MembershipRatePlanIds,
                          digipackIds: DigitalPackRatePlanIds,
                          weeklyPlans: WeeklyPlans,
                          catalogService: CatalogService[Future]
                        ) {

  case class RatePlan(ratePlanId: ProductRatePlanId, ratePlanName: String)
case class EnhancedRatePlan(ratePlanId: ProductRatePlanId, ratePlanName: String, price: Option[Float])
  def enhance(ratePlan: RatePlan):EnhancedRatePlan={
    val plan = find(ratePlan.ratePlanId)
    EnhancedRatePlan(ratePlan.ratePlanId,ratePlan.ratePlanName,plan.map(_.charges.gbpPrice.amount))
  }

  implicit val prpidWrite = new Writes[ProductRatePlanId] {
    def writes(productRatePlanId: ProductRatePlanId): JsValue = {
      JsString(productRatePlanId.get)
    }
  }
  implicit val ratePlanWrite = Json.writes[RatePlan]
  implicit val eratePlanWrite = Json.writes[EnhancedRatePlan]


  def find(productRatePlanId: ProductRatePlanId) = {
    lazy val catalog = catalogService.unsafeCatalog
    catalog.paid.find(_.id == productRatePlanId)
  }

  def all = googleAuthAction {
    Ok(Json.obj(
      Membership.id -> Json.toJson(Seq(
        RatePlan(membershipIds.friend, "Friend"),
        RatePlan(membershipIds.supporterMonthly, "Supporter monthly"),
        RatePlan(membershipIds.supporterYearly, "Supporter yearly"),
        RatePlan(membershipIds.partnerMonthly, "Partner monthly"),
        RatePlan(membershipIds.partnerYearly, "Partner yearly"),
        RatePlan(membershipIds.patronMonthly, "Patron monthly"),
        RatePlan(membershipIds.patronYearly, "Patron yearly")
      ).map(enhance)),
      DigitalPack.id -> Json.toJson(Seq(
        RatePlan(digipackIds.digitalPackMonthly, "Digital pack monthly"),
        RatePlan(digipackIds.digitalPackQuaterly, "Digital pack quarterly"),
        RatePlan(digipackIds.digitalPackYearly, "Digital pack yearly")
      ).map(enhance)),
      Newspaper.id -> Json.toJson(Seq(
        RatePlan(paperPlans.delivery.sundayplus, "Home Delivery Sunday+"),
        RatePlan(paperPlans.delivery.weekend, "Home Delivery Weekend"),
        RatePlan(paperPlans.delivery.weekendplus, "Home Delivery Weekend+"),
        RatePlan(paperPlans.delivery.sixday, "Home Delivery Sixday"),
        RatePlan(paperPlans.delivery.sixdayplus, "Home Delivery Sixday+"),
        RatePlan(paperPlans.delivery.everyday, "Home Delivery Everyday"),
        RatePlan(paperPlans.delivery.everydayplus, "Home Delivery Everyday+"),
        RatePlan(paperPlans.voucher.sundayplus, "Voucher Sunday+"),
        RatePlan(paperPlans.voucher.weekend, "Voucher Weekend"),
        RatePlan(paperPlans.voucher.weekendplus, "Voucher Weekend+"),
        RatePlan(paperPlans.voucher.sixday, "Voucher Sixday"),
        RatePlan(paperPlans.voucher.sixdayplus, "Voucher Sixday+"),
        RatePlan(paperPlans.voucher.everyday, "Voucher Everyday"),
        RatePlan(paperPlans.voucher.everydayplus, "Voucher Everyday+")
      ).map(enhance)),
      // TODO - Guardian Weekly - One Year
      GuardianWeekly.id -> Json.toJson(Seq(
        RatePlan(weeklyPlans.zoneA.yearly, "Weekly Zone A Yearly"),
        RatePlan(weeklyPlans.zoneA.quarterly, "Weekly Zone A Quarterly"),
        RatePlan(weeklyPlans.zoneB.yearly, "Weekly Zone B Yearly"),
        RatePlan(weeklyPlans.zoneB.quarterly, "Weekly Zone B Quarterly")
      ).map(enhance))
    ))
  }
}
