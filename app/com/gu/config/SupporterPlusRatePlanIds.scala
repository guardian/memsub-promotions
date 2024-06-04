package com.gu.config

import com.gu.memsub.Subscription.ProductRatePlanId

case class SupporterPlusRatePlanIds(
    yearly: ProductRatePlanId,
    monthly: ProductRatePlanId,
    guardianWeeklyRestOfWorldMonthly: ProductRatePlanId,
    guardianWeeklyRestOfWorldAnnual: ProductRatePlanId,
    guardianWeeklyDomesticAnnual: ProductRatePlanId,
    guardianWeeklyDomesticMonthly: ProductRatePlanId,
) extends ProductFamilyRatePlanIds {
  override val productRatePlanIds: Set[ProductRatePlanId] =
    Set(
      yearly,
      monthly,
      guardianWeeklyRestOfWorldMonthly,
      guardianWeeklyRestOfWorldAnnual,
      guardianWeeklyDomesticAnnual,
      guardianWeeklyDomesticMonthly,
    )
}

object SupporterPlusRatePlanIds {
  def fromConfig(config: com.typesafe.config.Config): SupporterPlusRatePlanIds =
    SupporterPlusRatePlanIds(
      ProductRatePlanId(config.getString("yearly")),
      ProductRatePlanId(config.getString("monthly")),
      ProductRatePlanId(config.getString(("guardianWeeklyRestOfWorldMonthly"))),
      ProductRatePlanId(config.getString(("guardianWeeklyRestOfWorldAnnual"))),
      ProductRatePlanId(config.getString(("guardianWeeklyDomesticAnnual"))),
      ProductRatePlanId(config.getString(("guardianWeeklyDomesticMonthly"))),
    )
}
