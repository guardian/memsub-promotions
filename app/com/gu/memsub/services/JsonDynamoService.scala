package com.gu.memsub.services

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.spec.{GetItemSpec, ScanSpec}
import com.amazonaws.services.dynamodbv2.document._
import com.gu.aws.CredentialsProvider
import play.api.libs.json._
import scala.jdk.CollectionConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scalaz.Monad

class JsonDynamoService[A, M[_]](table: Table)(implicit m: Monad[M]) {

  implicit val itemFormat = JsonDynamoService.itemFormat

  def all(implicit formatter: Reads[A]): M[Seq[A]] = Monad[M].point {
    val items: Iterator[Item] = table.scan().iterator().asScala
    items.flatMap(i => Json.fromJson[A](Json.toJson[Item](i)).asOpt).toList
  }

  def add(p: A)(implicit formatter: Writes[A]): M[Unit] = Monad[M].point {
    val item = Json.fromJson[Item](Json.toJson(p))
      .getOrElse(throw new IllegalStateException(s"Unable to convert $p to item"))
    table.putItem(item)
    ()
  }

  def find[B](b: B)(implicit of: OWrites[B], r: Reads[A]): M[List[A]] = Monad[M].point {
    val primaryKey = table.describe().getKeySchema.get(0).getAttributeName
    val jsonItem = Json.toJson(b)
    val dynamoResult = (jsonItem \ primaryKey).validate[String].asOpt.fold {
      itemFormat.reads(jsonItem).fold(err => Seq.empty, { itemFromJson =>
        val filters = itemFromJson.asMap().asScala.map { case (k, v) => new ScanFilter(k).eq(v): ScanFilter }.toSeq
        table.scan(new ScanSpec().withScanFilters(filters:_*)).iterator().asScala.toSeq
      })
    } { keyValue =>
      Option(table.getItem(new GetItemSpec().withPrimaryKey(primaryKey, keyValue))).toSeq
    }
    dynamoResult.flatMap(i => Json.fromJson[A](Json.toJson[Item](i)).asOpt).toList
  }
}

object JsonDynamoService {

  val itemFormat = Format(
    (in: JsValue) => Try(JsSuccess(Item.fromJSON(in.toString))).getOrElse(JsError(s"unable to deserialise $in")),
    (o: Item) => Json.parse(o.toJSON)
  )

  def forTable[A](table: String)(implicit e: ExecutionContext): JsonDynamoService[A, Future] = {
    import scalaz.std.scalaFuture._
    val dynamoDBClient = AmazonDynamoDBClient.builder
      .withCredentials(CredentialsProvider)
      .withRegion(Regions.EU_WEST_1)
      .build()
    new JsonDynamoService[A, Future](new DynamoDB(dynamoDBClient).getTable(table))(futureInstance)
  }

}
