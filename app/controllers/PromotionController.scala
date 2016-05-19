package controllers

import java.util.UUID
import com.gu.memsub.promo._
import com.gu.memsub.promo.Promotion.AnyPromotion
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.memsub.services.JsonDynamoService
import com.gu.memsub.promo.Formatters.PromotionFormatters._
import com.gu.memsub.promo.Formatters.Common._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Result}
import scala.concurrent.Future
import play.api.mvc.Results._
import scala.util.Try

class PromotionController(service: JsonDynamoService[AnyPromotion, Future]) {

  def all(campaignCode: Option[String]) = Action.async {
    campaignCode.map(CampaignCode).fold(service.all)(service.find).map(promos => Ok(Json.toJson(promos)))
  }

  def get(uuid: Option[String]) = Action.async {
    uuid.flatMap(i => Try(UUID.fromString(i)).toOption).map {
      i => service.find(i).map(_.headOption.fold[Result](NotFound)(promo => Ok(Json.toJson(promo))))
    }.getOrElse[Future[Result]](Future.successful(BadRequest))
  }

  def upsert = Action.async { request =>
    request.body.asJson.map { json =>
      json.validate[AnyPromotion].map { promotion => {
        service.add(promotion).map(_ => Ok(Json.obj("status" -> "ok")))
      }
      }.recoverTotal{
        e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
    }.getOrElse {
      Future(BadRequest("Could not parse campaign data"))
    }
  }
}
