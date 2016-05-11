package wiring
import com.gu.config.{DigitalPackRatePlanIds, MembershipRatePlanIds}
import com.gu.memsub.services.CatalogService
import play.api.BuiltInComponents
import play.api.libs.concurrent.Execution.Implicits._
import com.typesafe.config.{Config, ConfigFactory}
import com.softwaremill.macwire._
import controllers._
import play.api.routing.Router
import router.Routes
import wiring.AppComponents.Stage

class AppComponents(private val stage: Stage, builtInComponents: BuiltInComponents) {

  import builtInComponents._
  lazy val config = ConfigFactory.load()
  lazy val promoService = com.gu.memsub.services.PromoStorageService.forStage(config, stage.name)

  val ratePlanPath = s"touchpoint.backend.environments.${stage.name}.zuora.ratePlanIds"
  lazy val membershipRatePlanIds = MembershipRatePlanIds.fromConfig(config.getConfig(ratePlanPath + ".membership"))
  lazy val digipackRatePlanIds = DigitalPackRatePlanIds.fromConfig(config.getConfig(ratePlanPath + ".digitalpack"))

  lazy val healthController = wire[HealthCheckController]
  lazy val promoController = wire[PromotionController]
  lazy val planController = wire[RatePlanController]
  lazy val homeController = wire[StaticController]
  lazy val assetController = wire[Assets]

  val prefix: String = "/"
  lazy val router: Router = wire[Routes]

}

object AppComponents {
  sealed trait Stage { def name: String }
  case object DEV extends Stage { override def name = "DEV" }
  case object UAT extends Stage { override def name = "UAT" }
  case object PROD extends Stage { override def name = "PROD" }
}