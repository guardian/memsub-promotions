package controllers

import com.gu.memsub.promo.Campaign
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.memsub.services.{JsonDynamoService}
import com.gu.memsub.promo.Formatters._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Action
import scala.concurrent.Future
import play.api.mvc.Results._

class CampaignController(service: JsonDynamoService[Campaign, Future]) {

  def all = Action.async {
    service.all.map(campaigns => Ok(Json.toJson(campaigns)))
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
