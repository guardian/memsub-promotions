package controllers

import java.util.{TimeZone, UUID}

import actions.GoogleAuthAction._
import com.gu.memsub.promo.Formatters.Common._
import com.gu.memsub.promo.Formatters.PromotionFormatters._
import com.gu.memsub.promo.Promotion.AnyPromotion
import com.gu.memsub.promo._
import com.gu.memsub.services.JsonDynamoService
import com.gu.memsub.subsv2.services.CatalogService
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTimeZone
import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError}
import play.api.mvc.Result
import play.api.mvc.Results._
import wiring.AppComponents.Stage
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class PromotionController(
  stage: Stage,
  googleAuthAction: GoogleAuthenticatedAction,
  catalogService: CatalogService[Future],
  dynamoService: JsonDynamoService[AnyPromotion, Future],
  implicit val ec: ExecutionContext
) extends LazyLogging {

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
    campaignCode.map(CampaignCode).fold(dynamoService.all)(dynamoService.find).map(promos => Ok(Json.toJson(promos.sortBy(_.name))))
  }

  def get(uuid: Option[String]) = googleAuthAction.async {
    uuid.flatMap(i => Try(UUID.fromString(i)).toOption).map {
      i => dynamoService.find(i).map(_.headOption.fold[Result](NotFound)(promo => Ok(Json.toJson(promo))))
    }.getOrElse[Future[Result]](Future.successful(BadRequest))
  }

  def productRatePlanIdsAreValidForStage(promo: AnyPromotion): Boolean = promo.appliesTo.productRatePlanIds.forall {
    productRatePlanId => {
      val productRatePlanIdIsInCatalog = catalogService.unsafeCatalog.paid.exists(_.id == productRatePlanId)
      if (!productRatePlanIdIsInCatalog) {
        logger.info(s"$productRatePlanId was not found in the $stage catalog")
      }
      productRatePlanIdIsInCatalog
    }
  }

  def validate = googleAuthAction { request =>
    val jsonValidationAttempt = for {
      jsonToTest <- request.body.asJson.toRight[Seq[(JsPath, Seq[JsonValidationError])]](Seq.empty).right
      promo <- Json.fromJson[AnyPromotion](jsonToTest).asEither.right
    } yield promo

    jsonValidationAttempt match {
      case Right(promo) if productRatePlanIdsAreValidForStage(promo) =>
        Ok(Json.obj("status" -> "ok"))
      case Right(promo) =>
        logger.warn(s"Failed to validate promotion $jsonValidationAttempt against $stage catalog")
        InternalServerError
      case Left(error) =>
        logger.warn(s"Failed to parse promotion JSON correctly due to $error")
        BadRequest
    }

  }

  def upsert = googleAuthAction.async { request =>
    request.body.asJson.map { json =>
      json.validate[AnyPromotion].map { promotion => {
          dynamoService.add(normaliseDateTimes(promotion)).map(_ => Ok(Json.obj("status" -> "ok")))
        }
      }.recoverTotal{
        e => Future.successful(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
    }.getOrElse {
      Future.successful(BadRequest("Could not parse campaign data"))
    }
  }
}
