package controllers

import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.config.{DigitalPackRatePlanIds, SupporterPlusRatePlanIds, TierThreeRatePlanIds}
import com.gu.i18n.Currency
import com.gu.i18n.Currency.{AUD, CAD, EUR, GBP, USD}
import com.gu.memsub.Price
import com.gu.memsub.Subscription.ProductRatePlanId
import com.gu.memsub.promo.CampaignGroup.{DigitalPack, GuardianWeekly, Newspaper, SupporterPlus}
import com.gu.memsub.subsv2.services.CatalogService
import com.typesafe.scalalogging.LazyLogging
import conf.{PaperProducts, WeeklyPlans}
import controllers.RatePlanController._
import play.api.libs.json._
import play.api.mvc.Results._

import scala.concurrent.Future
import com.gu.memsub.Benefit.TierThree

case class RatePlan(ratePlanId: ProductRatePlanId, ratePlanName: String)

case class EnhancedRatePlan(
    ratePlanId: ProductRatePlanId,
    ratePlanName: String,
    price: Option[String],
    priceSummary: Option[Iterable[Price]],
    description: Option[String],
    period: Option[Int],
)

class RatePlanController(
    googleAuthAction: GoogleAuthenticatedAction,
    paperPlans: PaperProducts,
    digipackIds: DigitalPackRatePlanIds,
    supporterPlusIds: SupporterPlusRatePlanIds,
    tierThreeIds: TierThreeRatePlanIds,
    weeklyPlans: WeeklyPlans,
    catalogService: CatalogService[Future],
) {

  def sortCurrency = (price: Price) =>
    price.currency match {
      case GBP => 0
      case USD => 1
      case AUD => 2
      case EUR => 3
      case CAD => 4
      case _ => 5
    }

  def enhance(ratePlan: RatePlan): EnhancedRatePlan = {
    val plan = find(ratePlan.ratePlanId)
    EnhancedRatePlan(
      ratePlan.ratePlanId,
      ratePlan.ratePlanName,
      plan.map(_.charges.gbpPrice.prettyAmount),
      plan.map(_.charges.price.prices.toList.sortBy(sortCurrency)),
      plan.map(_.description),
      plan.map(_.charges.billingPeriod.monthsInPeriod),
    )
  }

  lazy val catalog = catalogService.unsafeCatalog

  def find(productRatePlanId: ProductRatePlanId) = {
    catalog.paid.find(_.id == productRatePlanId)
  }

  def all = googleAuthAction {
    Ok(
      Json.obj(
        SupporterPlus.id -> Json.toJson(
          Seq(
            RatePlan(supporterPlusIds.yearly, "Annual"),
            RatePlan(supporterPlusIds.monthly, "Monthly"),
          ).map(enhance),
        ),
        TierThree.id -> Json.toJson(
          Seq(
            RatePlan(tierThreeIds.domesticAnnual, "Domestic Annual"),
            RatePlan(tierThreeIds.domesticMonthly, "Domestic Monthly"),
            RatePlan(tierThreeIds.restOfWorldMonthly, "RestOfWorld Monthly"),
            RatePlan(tierThreeIds.restOfWorldAnnual, "RestOfWorld Annual"),
            RatePlan(tierThreeIds.domesticAnnualV2, "Domestic Annual V2"),
            RatePlan(tierThreeIds.domesticMonthlyV2, "Domestic Monthly V2"),
            RatePlan(tierThreeIds.restOfWorldMonthlyV2, "RestOfWorld Monthly V2"),
            RatePlan(tierThreeIds.restOfWorldAnnualV2, "RestOfWorld Annual V2"),
          ).map(enhance),
        ),
        DigitalPack.id -> Json.toJson(
          Seq(
            RatePlan(digipackIds.digitalPackMonthly, "Digital Pack monthly"),
            RatePlan(digipackIds.digitalPackQuaterly, "Digital Pack quarterly"),
            RatePlan(digipackIds.digitalPackYearly, "Digital Pack yearly"),
          ).map(enhance),
        ),
        Newspaper.id -> Json.toJson(
          Seq(
            RatePlan(paperPlans.delivery.saturdayplus, "Home Delivery Saturday+"),
            RatePlan(paperPlans.delivery.sunday, "Home Delivery Observer"),
            RatePlan(paperPlans.delivery.weekendplus, "Home Delivery Weekend+"),
            RatePlan(paperPlans.delivery.sixdayplus, "Home Delivery Sixday+"),
            RatePlan(paperPlans.delivery.everydayplus, "Home Delivery Everyday+"),
            RatePlan(paperPlans.nationalDelivery.weekendplus, "National Delivery Weekend+"),
            RatePlan(paperPlans.nationalDelivery.sixdayplus, "National Delivery Sixday+"),
            RatePlan(paperPlans.nationalDelivery.everydayplus, "National Delivery Everyday+"),
            RatePlan(paperPlans.digitalVoucher.saturdayplus, "Subscription Card Saturday+"),
            RatePlan(paperPlans.digitalVoucher.sunday, "Subscription Card Observer"),
            RatePlan(paperPlans.digitalVoucher.weekendplus, "Subscription Card Weekend+"),
            RatePlan(paperPlans.digitalVoucher.sixdayplus, "Subscription Card Sixday+"),
            RatePlan(paperPlans.digitalVoucher.everydayplus, "Subscription Card Everyday+"),
          ).map(enhance),
        ),
        GuardianWeekly.id -> Json.toJson(
          (
            Seq(
              RatePlan(weeklyPlans.domestic.yearly, "Domestic Annual"),
              RatePlan(weeklyPlans.row.yearly, "ROW Annual"),
              RatePlan(weeklyPlans.domestic.quarterly, "Domestic Quarterly"),
              RatePlan(weeklyPlans.row.quarterly, "ROW Quarterly"),
              RatePlan(weeklyPlans.domestic.monthly, "Domestic Monthly"),
              RatePlan(weeklyPlans.row.monthly, "ROW Monthly"),
            )
              ++ weeklyPlans.domestic.six.map(id => RatePlan(id, "Domestic 6-for-6"))
              ++ weeklyPlans.row.six.map(id => RatePlan(id, "ROW 6-for-6"))
              ++ weeklyPlans.domestic.oneYear.map(id => RatePlan(id, "Domestic 1 year fixed"))
              ++ weeklyPlans.row.oneYear.map(id => RatePlan(id, "ROW 1 year fixed"))
              ++ weeklyPlans.domestic.threeMonth.map(id => RatePlan(id, "Domestic 3 month fixed"))
              ++ weeklyPlans.row.threeMonth.map(id => RatePlan(id, "ROW 3 month fixed"))
          )
            .map(enhance),
        ),
      ),
    )
  }
}

object RatePlanController {
  implicit val prpidWrite: Writes[ProductRatePlanId] = (productRatePlanId: ProductRatePlanId) =>
    JsString(productRatePlanId.get)
  implicit val ratePlanWrite: OWrites[RatePlan] = Json.writes[RatePlan]
  implicit val priceWrite: Writes[Price] = (price: Price) =>
    Json.obj(("currency", Json.toJson(price.currency)), ("amount", JsString(price.amount.toString)))
  implicit val currencyWrite: Writes[Currency] = (currency: Currency) => JsString(currency.iso)
  implicit val eratePlanWrite: OWrites[EnhancedRatePlan] = Json.writes[EnhancedRatePlan]
}
