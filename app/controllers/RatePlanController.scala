package controllers

import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.i18n.Currency
import com.gu.memsub.Price
import com.gu.memsub.Subscription.ProductRatePlanId
import com.gu.memsub.promo.CampaignGroup.{DigitalPack, GuardianWeekly, Newspaper}
import com.gu.memsub.subsv2.services.CatalogService
import com.typesafe.scalalogging.LazyLogging
import conf.{PaperProducts, WeeklyPlans}
import controllers.RatePlanController._
import play.api.libs.json._
import play.api.mvc.Results._

import scala.concurrent.Future

case class RatePlan(ratePlanId: ProductRatePlanId, ratePlanName: String)

case class EnhancedRatePlan(ratePlanId: ProductRatePlanId, ratePlanName: String, price: Option[String], priceSummary: Option[Iterable[Price]], description: Option[String],period:Option[Int])

class RatePlanController(
    googleAuthAction: GoogleAuthenticatedAction,
    paperPlans: PaperProducts,
    membershipIds: MembershipRatePlanIds,
    digipackIds: DigitalPackRatePlanIds,
    weeklyPlans: WeeklyPlans,
    catalogService: CatalogService[Future]
  ) {

  def enhance(ratePlan: RatePlan): EnhancedRatePlan = {
    val plan = find(ratePlan.ratePlanId)
    EnhancedRatePlan(
      ratePlan.ratePlanId,
      ratePlan.ratePlanName,
      plan.map(_.charges.gbpPrice.prettyAmount),
      plan.map(_.charges.price.prices),
      plan.map(_.description),
      plan.map(_.charges.billingPeriod.monthsInPeriod)
    )
  }


  lazy val catalog = catalogService.unsafeCatalog

  def find(productRatePlanId: ProductRatePlanId) = {
    catalog.paid.find(_.id == productRatePlanId)
  }

  def all = googleAuthAction {
    Ok(Json.obj(
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
          RatePlan(weeklyPlans.domestic.yearly, "Domestic Annual"),
          RatePlan(weeklyPlans.row.yearly, "ROW Annual"),
          RatePlan(weeklyPlans.domestic.quarterly, "Domestic Quarterly"),
          RatePlan(weeklyPlans.row.quarterly, "ROW Quarterly")
        )
          ++ weeklyPlans.domestic.six.map(id => RatePlan(id, "Domestic 6-for-6"))
          ++ weeklyPlans.row.six.map(id => RatePlan(id, "ROW 6-for-6"))
          ++ weeklyPlans.domestic.oneYear.map(id => RatePlan(id, "Domestic 1 year fixed"))
          ++ weeklyPlans.row.oneYear.map(id => RatePlan(id, "ROW 1 year fixed"))
          ++ weeklyPlans.domestic.threeMonth.map(id => RatePlan(id, "Domestic 3 month fixed"))
          ++ weeklyPlans.row.threeMonth.map(id => RatePlan(id, "ROW 3 month fixed"))
        )
        .map(enhance)
      )
    ))
  }
}

object RatePlanController {
  implicit val prpidWrite: Writes[ProductRatePlanId] = (productRatePlanId: ProductRatePlanId) => JsString(productRatePlanId.get)
  implicit val ratePlanWrite: OWrites[RatePlan] = Json.writes[RatePlan]
  implicit val priceWrite: Writes[Price] = (price: Price) => Json.obj(("currency", Json.toJson(price.currency)), ("amount", JsString(price.amount.toString)))
  implicit val currencyWrite: Writes[Currency] = (currency: Currency) => JsString(currency.iso)
  implicit val eratePlanWrite: OWrites[EnhancedRatePlan] = Json.writes[EnhancedRatePlan]
}
