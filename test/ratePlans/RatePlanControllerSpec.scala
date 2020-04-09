package ratePlans

import com.gu.i18n.Currency.GBP
import com.gu.memsub.Price
import com.gu.memsub.Subscription.ProductRatePlanId
import controllers.EnhancedRatePlan
import controllers.RatePlanController._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class RatePlanControllerSpec extends PlaySpec {

  "RatePlanController" must {
    "serialise rate plans" in {
      val price = Price(19F, GBP)

      val erp = EnhancedRatePlan(
        ProductRatePlanId("blah"),
        "Test plan",
        Some("19"),
        Some(List(price)),
        Some("description"),
        Some(3)
      )
      val json = Json.toJson(erp)

      val expected = Json.parse(
        """
          {
              "ratePlanId": "blah",
              "ratePlanName": "Test plan",
              "price": "19",
              "priceSummary": [
                  {
                      "currency": "GBP",
                      "amount": "19.0"
                  }
              ],
              "description": "description",
              "period": 3
          }
        """)
      json mustBe expected
    }
  }
}
