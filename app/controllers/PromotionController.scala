package controllers
import play.api.libs.concurrent.Execution.Implicits._
import com.gu.memsub.services.PromoStorageService
import com.gu.memsub.promo.Formatters._
import play.api.libs.json.Json
import play.api.mvc.Action
import scala.concurrent.Future
import play.api.mvc.Results._

class PromotionController(service: PromoStorageService[Future]) {

  def all = Action.async {
    service.all.map(promos => Ok(Json.toJson(promos)))
  }
}
