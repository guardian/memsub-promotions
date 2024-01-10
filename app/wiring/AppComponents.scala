package wiring
import com.gu.config.DigitalPackRatePlanIds
import com.gu.googleauth.AuthAction
import com.gu.memsub.auth.common.MemSub.Google.googleAuthConfigFor
import com.gu.memsub.promo.{Campaign, DynamoTables}
import com.gu.memsub.promo.Promotion.AnyPromotion
import play.api.BuiltInComponentsFromContext
import com.typesafe.config.{Config, ConfigFactory}
import com.softwaremill.macwire._
import com.typesafe.scalalogging.LazyLogging
import conf.{CatalogService, PaperProducts, WeeklyPlans}
import controllers._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.{AnyContent, EssentialFilter}
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFFilter
import play.filters.hosts.AllowedHostsFilter
import wiring.AppComponents.Stage
import router.Routes

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with AhcWSComponents with AssetsComponents with HttpFiltersComponents {

  override def httpFilters: Seq[EssentialFilter] =
    super.httpFilters.filterNot { filter =>
      filter.getClass == classOf[AllowedHostsFilter] || filter.getClass == classOf[CSRFFilter]
    }

  lazy val config = ConfigFactory.load()
  lazy val stage = AppComponents.getStage(config)

  lazy val paperPlans = wireWith[Config, Stage, PaperProducts](PaperProducts.fromConfig)

  lazy val promoService = com.gu.memsub.services.JsonDynamoService.forTable[AnyPromotion](DynamoTables.promotions(config, stage.name))
  lazy val campaignService = com.gu.memsub.services.JsonDynamoService.forTable[Campaign](DynamoTables.campaigns(config, stage.name))

  lazy val catalog = CatalogService.fromConfig(config,stage.name)

  lazy val digipackRatePlanIds = DigitalPackRatePlanIds.fromConfig(config.getConfig(AppComponents.ratePlanPath(stage) + ".digitalpack"))
  lazy val weeklyRatePlanIds = WeeklyPlans.fromConfig(config, stage)

  private val googleAuthConfig = googleAuthConfigFor(config, httpConfiguration)

  val authAction = new AuthAction[AnyContent](googleAuthConfig, routes.AuthController.loginAction, controllerComponents.parsers.default)

  lazy val authController = wire[AuthController]
  lazy val healthController = wire[HealthCheckController]
  lazy val promoController = wire[PromotionController]
  lazy val campaignController = wire[CampaignController]
  lazy val countryController = wire[CountryController]
  lazy val planController = wire[RatePlanController]
  lazy val homeController = wire[StaticController]
  lazy val assetController = wire[Assets]

  val prefix: String = "/"

  override lazy val router: Router = wire[Routes]

}

object AppComponents extends LazyLogging {
  def ratePlanPath(stage: Stage): String = s"touchpoint.backend.environments.${stage.name}.zuora.ratePlanIds"

  sealed trait Stage { def name: String }
  case object CODE extends Stage { override def name = "CODE" }
  case object PROD extends Stage { override def name = "PROD" }

  // Throws exception if stage is missing or invalid
  def getStage(config: Config): Stage = {
    val stage = config.getString("stage") match {
      case "DEV" => CODE
      case "CODE" => CODE
      case "PROD" => PROD
    }
    logger.info(s"Stage: $stage")
    stage
  }
}