package conf

import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class WeeklyPlans(
  domestic: WeeklySchedules,
  row: WeeklySchedules
)

case class WeeklySchedules(
  yearly: ProductRatePlanId,
  quarterly: ProductRatePlanId,
  monthly: ProductRatePlanId,
  six: Option[ProductRatePlanId],
  oneYear: Option[ProductRatePlanId],
  threeMonth: Option[ProductRatePlanId]
)


object WeeklyPlans {
  def plansFor(config: Config, product: String) = {
    WeeklySchedules(
      yearly = ProductRatePlanId(config.getString(s"weekly.$product.yearly")),
      quarterly = ProductRatePlanId(config.getString(s"weekly.$product.quarterly")),
      monthly = ProductRatePlanId(config.getString(s"weekly.$product.monthly")),
      six = if (config.hasPath(s"weekly.$product.six"))
        Some(ProductRatePlanId(config.getString(s"weekly.$product.six"))) else None,
      oneYear = if (config.hasPath(s"weekly.$product.oneyear"))
        Some(ProductRatePlanId(config.getString(s"weekly.$product.oneyear"))) else None,
      threeMonth = if (config.hasPath(s"weekly.$product.threemonths"))
        Some(ProductRatePlanId(config.getString(s"weekly.$product.threemonths"))) else None
    )
  }

  def fromConfig(config: Config, stage: Stage): WeeklyPlans = {
    val c = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    WeeklyPlans(
      domestic = plansFor(c, "domestic"),
      row = plansFor(c, "row")
    )
  }


}
