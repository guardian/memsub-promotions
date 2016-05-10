package wiring
import play.api.libs.concurrent.Execution.Implicits._
import com.typesafe.config.ConfigFactory
import play.api.BuiltInComponents
import com.softwaremill.macwire._
import controllers._

trait AppComponents { self: BuiltInComponents =>
  lazy val promoService = com.gu.memsub.services.PromoStorageService.forStage(ConfigFactory.load(), "DEV")
  lazy val healthController = wire[HealthCheckController]
  lazy val promoController = wire[PromotionController]
  lazy val homeController = wire[StaticController]
  lazy val assetController = wire[Assets]
}
