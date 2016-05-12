package controllers

import com.gu.memsub.promo.Campaign
import com.gu.memsub.promo.Promotion.AnyPromotion
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
    request.body.asJson.flatMap(_.validate[AnyPromotion].asOpt).fold[Future[Result]](Future.successful(BadRequest("No promotion")))(
      p => service.add(p).map(_ => Ok(Json.obj("status" -> "ok")))
    )
  }
}
