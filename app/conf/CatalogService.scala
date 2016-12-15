package conf

import com.gu.config.SubsV2ProductIds
import com.gu.memsub.subsv2
import com.gu.okhttp.RequestRunners.futureRunner
import com.gu.zuora.{ZuoraApiConfig, rest}
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.std.scalaFuture._

object CatalogService {
  def fromConfig(config: Config, env: String)={
    val simpleRestClient = new rest.SimpleClient[Future](ZuoraApiConfig.rest(config.getConfig(s"touchpoint.backend.environments.${env}"),env), futureRunner)

    new subsv2.services.CatalogService[Future](SubsV2ProductIds(config.getConfig(s"touchpoint.backend.environments.$env.zuora.productIds")), simpleRestClient, Await.result(_, 10.seconds), env)
  }

}
