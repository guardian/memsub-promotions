package conf
import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class PaperProducts(
  digitalVoucher: PaperPlans,
  voucher: PaperPlans,
  delivery: PaperPlans,
  nationalDelivery: NationalDeliveryPaperPlans
)

case class PaperPlans(
  saturday: ProductRatePlanId,
  saturdayplus: ProductRatePlanId,
  sunday: ProductRatePlanId,
  sundayplus: ProductRatePlanId,
  weekend: ProductRatePlanId,
  weekendplus: ProductRatePlanId,
  sixday: ProductRatePlanId,
  sixdayplus: ProductRatePlanId,
  everyday: ProductRatePlanId,
  everydayplus: ProductRatePlanId
)

case class NationalDeliveryPaperPlans(
  weekend: ProductRatePlanId,
  sixday: ProductRatePlanId,
  everyday: ProductRatePlanId,
)

object PaperProducts {

  def plansFor(config: Config, product: String) = PaperPlans(
    saturday = ProductRatePlanId(config.getString(s"$product.saturday")),
    saturdayplus = ProductRatePlanId(config.getString(s"$product.saturdayplus")),
    sunday = ProductRatePlanId(config.getString(s"$product.sunday")),
    sundayplus = ProductRatePlanId(config.getString(s"$product.sundayplus")),
    weekend = ProductRatePlanId(config.getString(s"$product.weekend")),
    weekendplus = ProductRatePlanId(config.getString(s"$product.weekendplus")),
    sixday = ProductRatePlanId(config.getString(s"$product.sixday")),
    sixdayplus = ProductRatePlanId(config.getString(s"$product.sixdayplus")),
    everyday = ProductRatePlanId(config.getString(s"$product.everyday")),
    everydayplus = ProductRatePlanId(config.getString(s"$product.everydayplus"))
  )

  def fromConfig(config: Config, stage: Stage): PaperProducts = {
    val stageConfig = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    PaperProducts(
      digitalVoucher = plansFor(stageConfig, "digitalVoucher"),
      voucher = plansFor(stageConfig, "voucher"),
      delivery = plansFor(stageConfig, "delivery"),
      nationalDelivery = NationalDeliveryPaperPlans(
        weekend = ProductRatePlanId(stageConfig.getString("nationalDelivery.weekend")),
        sixday = ProductRatePlanId(stageConfig.getString("nationalDelivery.sixday")),
        everyday = ProductRatePlanId(stageConfig.getString("nationalDelivery.everyday")),
      )
    )
  }
}
