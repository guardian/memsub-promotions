package utils

import com.github.nscala_time.time.Imports.DateTime
import com.gu.memsub.promo.CampaignGroup.GuardianWeekly
import com.gu.memsub.promo._
import org.scalatestplus.play.PlaySpec
import utils.CampaignUtils.sortCampaignsByPromotionDateThenNameForDisplay

class CampaignUtilsTest extends PlaySpec {

  private val anyCode = CampaignCode("foo")
  private val anyGroup = GuardianWeekly
  private val anyCampaignName = "Tracking Promotions"
  private val aRandomCampaign = Campaign(anyCode, anyGroup, anyCampaignName, None)
  private val aPromotion = Promotion.apply(
    name = "foo",
    description = "bar",
    appliesTo = AppliesTo(Set.empty, Set.empty),
    campaign = anyCode,
    channelCodes = Map.empty,
    landingPage = None,
    starts = new DateTime(0),
    expires = None,
    promotionType = Tracking
  )

  "sortCampaignsByPromotionDateThenNameForDisplay" must {
    "sort by name" in {
      val campaigns = Seq(
        aRandomCampaign.copy(name = "Tracking Promotions 1"),
        aRandomCampaign.copy(name = "Tracking Promotions 3"),
        aRandomCampaign.copy(name = "Tracking Promotions 2")
      )
      sortCampaignsByPromotionDateThenNameForDisplay(campaigns, Seq.empty) mustEqual Seq(
        aRandomCampaign.copy(name = "Tracking Promotions 1"),
        aRandomCampaign.copy(name = "Tracking Promotions 2"),
        aRandomCampaign.copy(name = "Tracking Promotions 3")
      )
    }
    "sort by date searching forward before it looks backward" in {
      val campaigns = Seq(
        aRandomCampaign.copy(code = CampaignCode("foo")),
        aRandomCampaign.copy(code = CampaignCode("no_promotions")),
        aRandomCampaign.copy(code = CampaignCode("bar")),
        aRandomCampaign.copy(code = CampaignCode("baz"))
      )
      val promotions = Seq(
        aPromotion.copy(campaign = CampaignCode("foo"), starts = new DateTime(1)),
        aPromotion.copy(campaign = CampaignCode("bar"), starts = new DateTime(2)),
        aPromotion.copy(campaign = CampaignCode("bar"), starts = new DateTime(4)),
        aPromotion.copy(campaign = CampaignCode("baz"), starts = new DateTime(6)),
      )
      sortCampaignsByPromotionDateThenNameForDisplay(campaigns, promotions, new DateTime(3)) mustEqual Seq(
        Campaign(CampaignCode("baz"), anyGroup, anyCampaignName, Some(new DateTime(6))), // forward from now
        Campaign(CampaignCode("bar"), anyGroup, anyCampaignName, Some(new DateTime(4))), // 4 not 2 as it looks forward from 3 first
        Campaign(CampaignCode("foo"), anyGroup, anyCampaignName, Some(new DateTime(1))), // back from now
        Campaign(CampaignCode("no_promotions"), anyGroup, anyCampaignName, None)
      )
    }
    "sort by both" in {
      val campaigns = Seq(
        aRandomCampaign.copy(code = CampaignCode("foo")),
        aRandomCampaign.copy(code = CampaignCode("no_promotions")),
        aRandomCampaign.copy(code = CampaignCode("bar")),
        aRandomCampaign.copy(code = CampaignCode("baz")),
        aRandomCampaign.copy(code = CampaignCode("xyz")),
      )
      val promotions = Seq(
        aPromotion.copy(campaign = CampaignCode("xyz"), starts = new DateTime(1)),
        aPromotion.copy(campaign = CampaignCode("bar"), starts = new DateTime(2)),
        aPromotion.copy(campaign = CampaignCode("bar"), starts = new DateTime(4)),
        aPromotion.copy(campaign = CampaignCode("baz"), starts = new DateTime(6)),
        aPromotion.copy(campaign = CampaignCode("foo"), starts = new DateTime(1))
      )
      sortCampaignsByPromotionDateThenNameForDisplay(campaigns, promotions, new DateTime(3)) mustEqual Seq(
        Campaign(CampaignCode("baz"), anyGroup, anyCampaignName, Some(new DateTime(6))), // forward from now
        Campaign(CampaignCode("bar"), anyGroup, anyCampaignName, Some(new DateTime(4))), // 4 not 2 as it looks forward from 3 first
        Campaign(CampaignCode("foo"), anyGroup, anyCampaignName, Some(new DateTime(1))), // back from now
        Campaign(CampaignCode("xyz"), anyGroup, anyCampaignName, Some(new DateTime(1))), // back from now
        Campaign(CampaignCode("no_promotions"), anyGroup, anyCampaignName, None)
      )
    }
  }
}
