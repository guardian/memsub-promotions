package wiring
import actions.GoogleAuthAction
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.memsub.promo.{Campaign, DynamoTables}
import com.gu.memsub.promo.Promotion.AnyPromotion
import play.api.BuiltInComponents
import play.api.libs.concurrent.Execution.Implicits._
import com.typesafe.config.{Config, ConfigFactory}
import com.softwaremill.macwire._
import conf.{CatalogService, PaperPlans, PaperProducts, WeeklyPlans}
import controllers._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import wiring.AppComponents.Stage
import router.Routes

class AppComponents(private val stage: Stage, c: BuiltInComponents with AhcWSComponents) {

  import c._
  lazy val config = ConfigFactory.load()
  lazy val paperPlans = wireWith[Config, Stage, PaperProducts](PaperProducts.fromConfig)

  lazy val promoService = com.gu.memsub.services.JsonDynamoService.forTable[AnyPromotion](DynamoTables.promotions(config, stage.name))
  lazy val campaignService = com.gu.memsub.services.JsonDynamoService.forTable[Campaign](DynamoTables.campaigns(config, stage.name))

  lazy val catalog = CatalogService.fromConf(config,stage.name)

  lazy val membershipRatePlanIds = MembershipRatePlanIds.fromConfig(config.getConfig(AppComponents.ratePlanPath(stage) + ".membership"))
  lazy val digipackRatePlanIds = DigitalPackRatePlanIds.fromConfig(config.getConfig(AppComponents.ratePlanPath(stage) + ".digitalpack"))
  lazy val weeklyRatePlanIds = WeeklyPlans.fromConfig(config, stage)

  lazy val googleAuthAction: GoogleAuthAction = wire[GoogleAuthAction]
  import googleAuthAction.GoogleAuthAction

  lazy val authController = wire[AuthController]
  lazy val healthController = wire[HealthCheckController]
  lazy val promoController = wire[PromotionController]
  lazy val campaignController = wire[CampaignController]
  lazy val countryController = wire[CountryController]
  lazy val planController = wire[RatePlanController]
  lazy val homeController = wire[StaticController]
  lazy val assetController = wire[Assets]


  val prefix: String = "/"
  lazy val router: Router = wire[Routes]

}

object AppComponents {
  def ratePlanPath(stage: Stage): String = s"touchpoint.backend.environments.${stage.name}.zuora.ratePlanIds"

  sealed trait Stage { def name: String }
  case object DEV extends Stage { override def name = "DEV" }
  case object UAT extends Stage { override def name = "UAT" }
  case object PROD extends Stage { override def name = "PROD" }
}