package controllers
import com.gu.memsub.promo.Formatters.Common._
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.i18n.{Country, CountryGroup, Currency, GBP}
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Results._

class CountryController(membershipIds: MembershipRatePlanIds, digipackIds: DigitalPackRatePlanIds) {

  def all = Action {

    implicit val curWrites = new Writes[Currency] {
      override def writes(in: Currency): JsValue = JsString(in.glyph)
    }

    implicit val cgWrites = Json.writes[CountryGroup]

    val overseasCountries = CountryGroup.UK.countries.filter(_ != Country.UK)

    val mainlandUk = CountryGroup("UK mainland", "ukm", Some(Country.UK), List(Country.UK), CountryGroup.UK.currency)
    val overseasUk = CountryGroup("UK overseas", "ukos", overseasCountries.headOption, overseasCountries, CountryGroup.UK.currency)

    val countryGroups = List(mainlandUk, overseasUk) ++ CountryGroup.allGroups.filter(_ != CountryGroup.UK)

    Ok(Json.toJson[List[CountryGroup]](countryGroups))
  }
}
