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

  case class EnhancedRatePlan(ratePlanId: ProductRatePlanId, ratePlanName: String, price: Option[String],description: Option[String],period:Option[Int])

  def enhance(ratePlan: RatePlan): EnhancedRatePlan = {
    val plan = find(ratePlan.ratePlanId)
    EnhancedRatePlan(ratePlan.ratePlanId, ratePlan.ratePlanName, plan.map(_.charges.gbpPrice.prettyAmount), plan.map(_.description), plan.map(_.charges.billingPeriod.monthsInPeriod))
  }

  implicit val prpidWrite = new Writes[ProductRatePlanId] {
    def writes(productRatePlanId: ProductRatePlanId): JsValue = {
      JsString(productRatePlanId.get)
    }
  }


  implicit val ratePlanWrite = Json.writes[RatePlan]
  implicit val eratePlanWrite = Json.writes[EnhancedRatePlan]

  lazy val catalog = catalogService.unsafeCatalog

  def find(productRatePlanId: ProductRatePlanId) = {
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
        RatePlan(digipackIds.digitalPackMonthly, "Digital Pack monthly"),
        RatePlan(digipackIds.digitalPackQuaterly, "Digital Pack quarterly"),
        RatePlan(digipackIds.digitalPackYearly, "Digital Pack yearly")
      ).map(enhance)),
      Newspaper.id -> Json.toJson(Seq(
        RatePlan(paperPlans.delivery.saturday, "Home Delivery Saturday"),
        RatePlan(paperPlans.delivery.saturdayplus, "Home Delivery Saturday+"),
        RatePlan(paperPlans.delivery.sunday, "Home Delivery Sunday"),
        RatePlan(paperPlans.delivery.sundayplus, "Home Delivery Sunday+"),
        RatePlan(paperPlans.delivery.weekend, "Home Delivery Weekend"),
        RatePlan(paperPlans.delivery.weekendplus, "Home Delivery Weekend+"),
        RatePlan(paperPlans.delivery.sixday, "Home Delivery Sixday"),
        RatePlan(paperPlans.delivery.sixdayplus, "Home Delivery Sixday+"),
        RatePlan(paperPlans.delivery.everyday, "Home Delivery Everyday"),
        RatePlan(paperPlans.delivery.everydayplus, "Home Delivery Everyday+"),
        RatePlan(paperPlans.voucher.saturday, "Voucher Saturday"),
        RatePlan(paperPlans.voucher.saturdayplus, "Voucher Saturday+"),
        RatePlan(paperPlans.voucher.sunday, "Voucher Sunday"),
        RatePlan(paperPlans.voucher.sundayplus, "Voucher Sunday+"),
        RatePlan(paperPlans.voucher.weekend, "Voucher Weekend"),
        RatePlan(paperPlans.voucher.weekendplus, "Voucher Weekend+"),
        RatePlan(paperPlans.voucher.sixday, "Voucher Sixday"),
        RatePlan(paperPlans.voucher.sixdayplus, "Voucher Sixday+"),
        RatePlan(paperPlans.voucher.everyday, "Voucher Everyday"),
        RatePlan(paperPlans.voucher.everydayplus, "Voucher Everyday+")
      ).map(enhance)),
      GuardianWeekly.id -> Json.toJson(
        (
        Seq(
          RatePlan(weeklyPlans.zoneA.yearly, "Zone A yearly (2017)"),
          RatePlan(weeklyPlans.zoneA.quarterly, "Zone A quarterly (2017)"),
          RatePlan(weeklyPlans.zoneB.yearly, "Zone B yearly (2015)"),
          RatePlan(weeklyPlans.zoneB.quarterly, "Zone B quarterly (2015)"),
          RatePlan(weeklyPlans.zoneC.yearly, "Zone C yearly (2017)"),
          RatePlan(weeklyPlans.zoneC.quarterly, "Zone C quarterly (2017)"),
          RatePlan(weeklyPlans.domestic.yearly, "Domestic yearly (2018)"),
          RatePlan(weeklyPlans.domestic.quarterly, "Domestic quarterly (2018)"),
          RatePlan(weeklyPlans.row.yearly, "ROW yearly (2018)"),
          RatePlan(weeklyPlans.row.quarterly, "ROW quarterly (2018)")
        )
          ++ weeklyPlans.domestic.oneYear.map(id => RatePlan(id, "Domestic 1 year (2018)"))
          ++ weeklyPlans.row.oneYear.map(id => RatePlan(id, "ROW 1 year (2018)"))
          ++ weeklyPlans.zoneA.six.map(id =>  RatePlan(id, "Zone A 6-for-6 (2017)"))
          ++ weeklyPlans.zoneC.six.map(id =>  RatePlan(id, "Zone C 6-for-6 (2017)"))
          ++ weeklyPlans.domestic.six.map(id => RatePlan(id, "6-for-6 Domestic (2018)"))
          ++ weeklyPlans.row.six.map(id => RatePlan(id, "6-for-6 ROW (2018)"))
          ++ weeklyPlans.zoneA.oneYear.map(id => RatePlan(id, "Zone A 1 year (Renewal - 2017)"))
          ++ weeklyPlans.zoneB.oneYear.map(id => RatePlan(id, "Zone B 1 year (Renewal - 2015)"))
          ++ weeklyPlans.zoneC.oneYear.map(id => RatePlan(id, "Zone C 1 year (Renewal - 2017)"))
        )
        .sortBy(_.ratePlanName)
        .map(enhance)
      )
    ))
  }
}
