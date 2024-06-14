package com.gu.config

import com.gu.memsub.Subscription.ProductRatePlanId

case class TierThreeRatePlanIds(
    domesticMonthly: ProductRatePlanId,
    domesticAnnual: ProductRatePlanId,
    restOfWorldMonthly: ProductRatePlanId,
    restOfWorldAnnual: ProductRatePlanId,
) extends ProductFamilyRatePlanIds {
  override val productRatePlanIds: Set[ProductRatePlanId] =
    Set(
      domesticMonthly,
      domesticAnnual,
      restOfWorldMonthly,
      restOfWorldAnnual,
    )
}

object TierThreeRatePlanIds {
  def fromConfig(config: com.typesafe.config.Config): TierThreeRatePlanIds =
    TierThreeRatePlanIds(
      ProductRatePlanId(config.getString("domesticMonthly")),
      ProductRatePlanId(config.getString("domesticAnnual")),
      ProductRatePlanId(config.getString("restOfWorldMonthly")),
      ProductRatePlanId(config.getString("restOfWorldAnnual")),
    )
}