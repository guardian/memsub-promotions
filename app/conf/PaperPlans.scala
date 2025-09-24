package conf
import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class PaperProducts(
    digitalVoucher: PaperPlans,
    delivery: PaperPlans,
    nationalDelivery: NationalDeliveryPaperPlans,
)

case class PaperPlans(
    saturdayplus: ProductRatePlanId,
    sunday: ProductRatePlanId,
    weekendplus: ProductRatePlanId,
    sixdayplus: ProductRatePlanId,
    everydayplus: ProductRatePlanId,
)

case class NationalDeliveryPaperPlans(
    weekendplus: ProductRatePlanId,
    sixdayplus: ProductRatePlanId,
    everydayplus: ProductRatePlanId,
)

object PaperProducts {

  def plansFor(config: Config, product: String) = PaperPlans(
    saturdayplus = ProductRatePlanId(config.getString(s"$product.saturdayplus")),
    sunday = ProductRatePlanId(config.getString(s"$product.sunday")),
    weekendplus = ProductRatePlanId(config.getString(s"$product.weekendplus")),
    sixdayplus = ProductRatePlanId(config.getString(s"$product.sixdayplus")),
    everydayplus = ProductRatePlanId(config.getString(s"$product.everydayplus")),
  )

  def fromConfig(config: Config, stage: Stage): PaperProducts = {
    val stageConfig = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    PaperProducts(
      digitalVoucher = plansFor(stageConfig, "digitalVoucher"),
      delivery = plansFor(stageConfig, "delivery"),
      nationalDelivery = NationalDeliveryPaperPlans(
        weekendplus = ProductRatePlanId(stageConfig.getString("nationalDelivery.weekendplus")),
        sixdayplus = ProductRatePlanId(stageConfig.getString("nationalDelivery.sixdayplus")),
        everydayplus = ProductRatePlanId(stageConfig.getString("nationalDelivery.everydayplus")),
      ),
    )
  }
}
