package controllers

import com.gu.memsub.ProductFamily
import com.gu.memsub.promo.Campaign
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.memsub.promo.Formatters.CampaignFormatters._
import com.gu.memsub.services.JsonDynamoService
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Action

import scala.concurrent.Future
import play.api.mvc.Results._
import wiring.AppComponents.Stage

class CampaignController(service: JsonDynamoService[Campaign, Future], stage: Stage) {

  def all(productFamily: Option[String]) = Action.async {
    val campaigns = productFamily.flatMap(ProductFamily.fromId).fold(service.all)(service.find)
    campaigns.map(c => Ok(Json.toJson(c)))
  }

  def upsert = Action.async { request =>
    request.body.asJson.map { json =>
      json.validate[Campaign].map { campaign => {
          service.add(campaign).map(_ => Ok(Json.obj("status" -> "ok")))
        }
      }.recoverTotal{
        e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
    }.getOrElse {
      Future(BadRequest("Could not parse campaign data"))
    }
  }

}
