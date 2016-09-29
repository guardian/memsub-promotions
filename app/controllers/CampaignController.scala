package controllers

import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.memsub.promo.Formatters.CampaignFormatters._
import com.gu.memsub.promo.{Campaign, CampaignCode, CampaignGroup}
import com.gu.memsub.services.JsonDynamoService
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Result
import play.api.mvc.Results._
import wiring.AppComponents.Stage

import scala.concurrent.Future

class CampaignController(googleAuthAction: GoogleAuthenticatedAction, service: JsonDynamoService[Campaign, Future], stage: Stage) {

  def all(group: Option[String]) = googleAuthAction.async {
    service.all.map(campaigns => {
      val filtered = group.flatMap(CampaignGroup.fromId).fold(campaigns)(group => campaigns.filter(_.group == group))
      Ok(Json.toJson(filtered.sortBy(_.name)))
    })
  }

  def get(code: Option[String]) = googleAuthAction.async {
    code.map(CampaignCode).map(c => service.find(c)).fold[Future[Result]](Future.successful(BadRequest)) { campaigns =>
      campaigns.map(_.headOption.fold[Result](NotFound)(c => Ok(Json.toJson(c))))
    }
  }

  def upsert = googleAuthAction.async { request =>
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
