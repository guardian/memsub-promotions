package controllers

import java.util.{TimeZone, UUID}

import actions.GoogleAuthAction._
import com.gu.memsub.promo.Formatters.Common._
import com.gu.memsub.promo.Formatters.PromotionFormatters._
import com.gu.memsub.promo.Promotion.AnyPromotion
import com.gu.memsub.promo._
import com.gu.memsub.services.JsonDynamoService
import org.joda.time.DateTimeZone
import play.api.data.validation.ValidationError
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, JsPath, Json}
import play.api.mvc.Result
import play.api.mvc.Results._

import scala.concurrent.Future
import scala.util.Try

class PromotionController(googleAuthAction: GoogleAuthenticatedAction, service: JsonDynamoService[AnyPromotion, Future]) {

  private val londonTimezone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/London"))

  /**
    * Only dates are shown on screen on the promo tool (with a timezone local to the user's laptop), so the start datetime
    * will be normalised to being at the start of the London day, and the end datetime will be normalised to being at the
    * end of the London day.
    * @param promotion the promotion object marshalled from the frontend POST
    * @return a copy of the promotion with its dates normalised.
    */
  private def normaliseDateTimes(promotion: AnyPromotion): AnyPromotion = {
    promotion.copy(
      starts = promotion.starts.withZone(londonTimezone).withTimeAtStartOfDay,
      expires = promotion.expires.map(_.withZone(londonTimezone).withTimeAtStartOfDay.plusDays(1).minusSeconds(1))
    )
  }

  def all(campaignCode: Option[String]) = googleAuthAction.async {
    campaignCode.map(CampaignCode).fold(service.all)(service.find).map(promos => Ok(Json.toJson(promos.sortBy(_.starts.getMillis).reverse)))
  }

  def get(uuid: Option[String]) = googleAuthAction.async {
    uuid.flatMap(i => Try(UUID.fromString(i)).toOption).map {
      i => service.find(i).map(_.headOption.fold[Result](NotFound)(promo => Ok(Json.toJson(promo))))
    }.getOrElse[Future[Result]](Future.successful(BadRequest))
  }

  def validate = googleAuthAction { request =>
    (for {
      jsonToTest <- request.body.asJson.toRight[Seq[(JsPath, Seq[ValidationError])]](Seq.empty).right
      promo <- Json.fromJson[AnyPromotion](jsonToTest).asEither.right
    } yield promo).fold(e => Ok(JsError.toJson(e)), p => Ok(Json.obj("status" -> "ok")))
  }

  def upsert = googleAuthAction.async { request =>
    request.body.asJson.map { json =>
      json.validate[AnyPromotion].map { promotion => {
        service.add(normaliseDateTimes(promotion)).map(_ => Ok(Json.obj("status" -> "ok")))
      }
      }.recoverTotal{
        e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
    }.getOrElse {
      Future(BadRequest("Could not parse campaign data"))
    }
  }
}
