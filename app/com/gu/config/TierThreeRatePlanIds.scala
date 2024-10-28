package com.gu.config

import com.gu.memsub.Subscription.ProductRatePlanId

case class TierThreeRatePlanIds(
    domesticMonthly: ProductRatePlanId,
    domesticAnnual: ProductRatePlanId,
    restOfWorldMonthly: ProductRatePlanId,
    restOfWorldAnnual: ProductRatePlanId,
    domesticMonthlyV2: ProductRatePlanId,
    domesticAnnualV2: ProductRatePlanId,
    restOfWorldMonthlyV2: ProductRatePlanId,
    restOfWorldAnnualV2: ProductRatePlanId,
) extends ProductFamilyRatePlanIds {
  override val productRatePlanIds: Set[ProductRatePlanId] =
    Set(
      domesticMonthly,
      domesticAnnual,
      restOfWorldMonthly,
      restOfWorldAnnual,
      domesticMonthlyV2,
      domesticAnnualV2,
      restOfWorldMonthlyV2,
      restOfWorldAnnualV2,
    )
}

object TierThreeRatePlanIds {
  def fromConfig(config: com.typesafe.config.Config): TierThreeRatePlanIds =
    TierThreeRatePlanIds(
      ProductRatePlanId(config.getString("domesticMonthly")),
      ProductRatePlanId(config.getString("domesticAnnual")),
      ProductRatePlanId(config.getString("restOfWorldMonthly")),
      ProductRatePlanId(config.getString("restOfWorldAnnual")),
      ProductRatePlanId(config.getString("domesticMonthlyV2")),
      ProductRatePlanId(config.getString("domesticAnnualV2")),
      ProductRatePlanId(config.getString("restOfWorldMonthlyV2")),
      ProductRatePlanId(config.getString("restOfWorldAnnualV2")),
    )
}