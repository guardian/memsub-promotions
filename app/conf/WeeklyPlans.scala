package conf

import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class WeeklyPlans(
                        zoneA: WeeklySchedules,
                        zoneB: WeeklySchedules,
                        zoneC: WeeklySchedules
                      )

case class WeeklySchedules(
                            yearly: ProductRatePlanId,
                            quarterly: ProductRatePlanId,
                            six: Option[ProductRatePlanId],
                            oneYear: ProductRatePlanId
                          )


object WeeklyPlans {
  def plansFor(config: Config, zone: String) = {
    WeeklySchedules(
      yearly = ProductRatePlanId(config.getString(s"weekly.$zone.yearly")),
      quarterly = ProductRatePlanId(config.getString(s"weekly.$zone.quarterly")),
      six = if (config.hasPath(s"weekly.$zone.six"))
        Option(ProductRatePlanId(config.getString(s"weekly.$zone.six"))) else None,
      oneYear = ProductRatePlanId(config.getString(s"weekly.$zone.oneyear"))
    )
  }

  def fromConfig(config: Config, stage: Stage): WeeklyPlans = {
    val c = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    WeeklyPlans(
      zoneA = plansFor(c, "zoneA"),
      zoneB = plansFor(c, "zoneB"),
      zoneC = plansFor(c, "zoneC")
    )
  }


}