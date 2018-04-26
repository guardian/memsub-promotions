package conf

import com.gu.config.SubsV2ProductIds
import com.gu.memsub.subsv2
import com.gu.memsub.subsv2.services.FetchCatalog
import com.typesafe.config.Config
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.std.scalaFuture._

object CatalogService {

  def fromConfig(config: Config, env: String) = {
    new subsv2.services.CatalogService[Future](
      SubsV2ProductIds(config.getConfig(s"touchpoint.backend.environments.$env.zuora.productIds")),
      FetchCatalog.fromS3(env),
      Await.result(_, 10.seconds),
      env)
  }

}
