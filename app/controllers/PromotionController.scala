package controllers

import com.gu.memsub.promo._
import com.gu.memsub.promo.Promotion.AnyPromotion
import org.joda.time.Days
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.memsub.services.{JsonDynamoService}
import com.gu.memsub.promo.Formatters._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Result, Action}
import scala.concurrent.Future
import play.api.mvc.Results._

class PromotionController(service: JsonDynamoService[AnyPromotion, Future]) {

  def all = Action.async {
    service.all.map(promos => Ok(Json.toJson(promos)))
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
