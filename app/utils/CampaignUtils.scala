package utils

import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Implicits.DateTimeOrdering
import com.gu.memsub.promo.{Campaign, CampaignGroup}
import com.gu.memsub.promo.Promotion.AnyPromotion

object CampaignUtils {
  def filterCampaignsByOptionalGroup(group: Option[CampaignGroup], campaigns: Seq[Campaign]): Seq[Campaign] = {
    group.map(group => campaigns.filter(_.group == group)) getOrElse campaigns
  }
  def sortCampaignsByPromotionDateThenNameForDisplay(campaigns: Seq[Campaign], promotions: Seq[AnyPromotion], now: DateTime = DateTime.now()): Seq[Campaign] = {
    campaigns.sortBy(_.name).reverse.map(campaign => {
      val campaignPromotionStartDateRange = promotions.filter(_.campaign == campaign.code).map(_.starts).sorted
      val earliestNextToStartDate = campaignPromotionStartDateRange.find(now.isBefore)
      val lastToStartDate = campaignPromotionStartDateRange.reverse.find(now.isAfter)
      val sortDate = earliestNextToStartDate orElse lastToStartDate
      campaign.copy(sortDate = sortDate)
    }).sortBy(_.sortDate).reverse
  }
}
