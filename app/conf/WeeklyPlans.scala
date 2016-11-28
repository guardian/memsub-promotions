package conf

import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class WeeklyPlans(
                        zoneA: WeeklySchedules,
                        zoneB: WeeklySchedules
                      )

case class WeeklySchedules(
                            yearly: ProductRatePlanId,
                            quarterly: ProductRatePlanId
                          )


object WeeklyPlans {
  def plansFor(config: Config, zone: String) = {
    WeeklySchedules(
      yearly = ProductRatePlanId(config.getString(s"weekly.$zone.yearly")),
      quarterly = ProductRatePlanId(config.getString(s"weekly.$zone.quarterly"))
    )
  }

  def fromConfig(config: Config, stage: Stage): WeeklyPlans = {
    val c = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    WeeklyPlans(
      zoneA = plansFor(c,"zoneA"),
      zoneB = plansFor(c,"zoneB")
    )
  }


}