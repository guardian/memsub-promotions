package conf
import com.gu.memsub.Subscription.ProductRatePlanId
import com.typesafe.config.Config
import wiring.AppComponents.Stage

case class PaperProducts(
  voucher: PaperPlans,
  delivery: PaperPlans
)

case class PaperPlans(
  sunday: ProductRatePlanId,
  sundayplus: ProductRatePlanId,
  weekend: ProductRatePlanId,
  weekendplus: ProductRatePlanId,
  sixday: ProductRatePlanId,
  sixdayplus: ProductRatePlanId,
  everyday: ProductRatePlanId,
  everydayplus: ProductRatePlanId
)

object PaperProducts {

  def plansFor(config: Config, product: String) = PaperPlans(
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
    val c = config.getConfig(s"touchpoint.backend.environments.${stage.name}")

    PaperProducts(
      voucher = plansFor(c, "voucher"),
      delivery = plansFor(c, "delivery")
    )
  }
}