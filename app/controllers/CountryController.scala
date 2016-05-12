package controllers
import com.gu.memsub.promo.Formatters.countryFormat
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.i18n.{Currency, Country, CountryGroup}
import play.api.libs.json._
import play.api.mvc.Action
import play.api.mvc.Results._

class CountryController(membershipIds: MembershipRatePlanIds, digipackIds: DigitalPackRatePlanIds) {

  def all = Action {

    implicit val curWrites = new Writes[Currency] {
      override def writes(in: Currency): JsValue = JsString(in.glyph)
    }

    implicit val cgWrites = Json.writes[CountryGroup]
    Ok(Json.toJson[List[CountryGroup]](CountryGroup.allGroups))
  }
}
