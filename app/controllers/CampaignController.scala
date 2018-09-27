package controllers

import actions.GoogleAuthAction.GoogleAuthenticatedAction
import com.gu.memsub.promo.Formatters.CampaignFormatters._
import com.gu.memsub.promo.Promotion.AnyPromotion
import com.gu.memsub.promo.{Campaign, CampaignCode, CampaignGroup}
import com.gu.memsub.services.JsonDynamoService
import utils.CampaignUtils.{filterCampaignsByOptionalGroup, sortCampaignsByPromotionDateThenNameForDisplay}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Result
import play.api.mvc.Results._
import wiring.AppComponents.Stage

import scala.concurrent.{ExecutionContext, Future}

class CampaignController(googleAuthAction: GoogleAuthenticatedAction, campaignService: JsonDynamoService[Campaign, Future], promotionService: JsonDynamoService[AnyPromotion, Future], stage: Stage, implicit val ec: ExecutionContext) {

  def all(group: Option[String]) = googleAuthAction.async {
    import com.gu.memsub.promo.Formatters.PromotionFormatters._

    val campaignsF = campaignService.all
    val promotionsF = promotionService.all
    for {
      campaigns <- campaignsF
      promotions <- promotionsF
    } yield {
      val filtered = filterCampaignsByOptionalGroup(group.flatMap(CampaignGroup.fromId), campaigns)
      val campaignsSortedByDateThenName = sortCampaignsByPromotionDateThenNameForDisplay(filtered, promotions)
      Ok(Json.toJson(campaignsSortedByDateThenName))
    }
  }

  def get(code: Option[String]) = googleAuthAction.async {
    code.map(CampaignCode).map(c => campaignService.find(c)).fold[Future[Result]](Future.successful(BadRequest)) { campaigns =>
      campaigns.map(_.headOption.fold[Result](NotFound)(c => Ok(Json.toJson(c))))
    }
  }

  def upsert = googleAuthAction.async { request =>
    request.body.asJson.map { json =>
      json.validate[Campaign].map { campaign => {
          campaignService.add(campaign).map(_ => Ok(Json.obj("status" -> "ok")))
        }
      }.recoverTotal{
        e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
      }
    }.getOrElse {
      Future(BadRequest("Could not parse campaign data"))
    }
  }

}
