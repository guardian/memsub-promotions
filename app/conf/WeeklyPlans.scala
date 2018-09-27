package conf

import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class WeeklyPlans(
                        zoneA: WeeklySchedules,
                        zoneB: WeeklySchedules,
                        zoneC: WeeklySchedules,
                        domestic: WeeklySchedules,
                        row: WeeklySchedules
                      )

case class WeeklySchedules(
                            yearly: ProductRatePlanId,
                            quarterly: ProductRatePlanId,
                            six: Option[ProductRatePlanId],
                            oneYear: Option[ProductRatePlanId]
                          )


object WeeklyPlans {
  def plansFor(config: Config, product: String) = {
    WeeklySchedules(
      yearly = ProductRatePlanId(config.getString(s"weekly.$product.yearly")),
      quarterly = ProductRatePlanId(config.getString(s"weekly.$product.quarterly")),
      six = if (config.hasPath(s"weekly.$product.six"))
        Some(ProductRatePlanId(config.getString(s"weekly.$product.six"))) else None,
      oneYear = if (config.hasPath(s"weekly.$product.oneYear"))
        Some(ProductRatePlanId(config.getString(s"weekly.$product.oneYear"))) else None
    )
  }

  def fromConfig(config: Config, stage: Stage): WeeklyPlans = {
    val c = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    WeeklyPlans(
      zoneA = plansFor(c, "zoneA"),
      zoneB = plansFor(c, "zoneB"),
      zoneC = plansFor(c, "zoneC"),
      domestic = plansFor(c, "domestic"),
      row = plansFor(c, "row")
    )
  }


}