package controllers
import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.memsub.promo.Formatters.Common._
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.i18n._
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Results._

class CountryController(googleAuthAction: GoogleAuthenticatedAction, membershipIds: MembershipRatePlanIds, digipackIds: DigitalPackRatePlanIds) {

  def all = googleAuthAction {

    implicit val curWrites = new Writes[Currency] {
      override def writes(in: Currency): JsValue = JsString(in.glyph)
    }

    implicit val pcWrites = new Writes[PostalCode] {
      override def writes(in: PostalCode): JsValue = JsString(in.name)
    }

    implicit val cgWrites = Json.writes[CountryGroup]

    val overseasCountries = CountryGroup.UK.countries.filter(_ != Country.UK)

    val mainlandUk = CountryGroup("UK mainland", "ukm", Some(Country.UK), List(Country.UK), CountryGroup.UK.currency, PostCode)
    val overseasUk = CountryGroup("UK overseas", "ukos", overseasCountries.headOption, overseasCountries, CountryGroup.UK.currency, PostCode)

    val countryGroups = List(mainlandUk, overseasUk) ++ CountryGroup.allGroups.filter(_ != CountryGroup.UK)

    Ok(Json.toJson[List[CountryGroup]](countryGroups))
  }
}
