package com.gu.memsub.services

import com.gu.aws.CredentialsProvider
import play.api.libs.json._
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model._

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scalaz.Monad

class JsonDynamoService[A, M[_]](tableName: String, client: DynamoDbClient)(implicit m: Monad[M]) {

  def all(implicit formatter: Reads[A]): M[Seq[A]] = Monad[M].point {
    val scanRequest = ScanRequest.builder().tableName(tableName).build()
    val items = client.scan(scanRequest).items().asScala
    items.flatMap(i => Json.fromJson[A](Json.parse(toJson(i))).asOpt).toList
  }

  def add(p: A)(implicit formatter: Writes[A]): M[Unit] = Monad[M].point {
    val json = Json.toJson(p)
    val item = toAttributeValueMap(json)
    val putRequest = PutItemRequest.builder()
      .tableName(tableName)
      .item(item.asJava)
      .build()
    client.putItem(putRequest)
    ()
  }

  def find[B](b: B)(implicit of: OWrites[B], r: Reads[A]): M[List[A]] = Monad[M].point {
    val describeRequest = DescribeTableRequest.builder().tableName(tableName).build()
    val primaryKey = client.describeTable(describeRequest).table().keySchema().get(0).attributeName()
    val jsonItem = Json.toJson(b)

    val dynamoResult = (jsonItem \ primaryKey).validate[String].asOpt.fold {
      val item = toAttributeValueMap(jsonItem)
      val scanRequest = ScanRequest.builder()
        .tableName(tableName)
        .scanFilter(item.map { case (k, v) =>
          k -> Condition.builder()
            .comparisonOperator(ComparisonOperator.EQ)
            .attributeValueList(v)
            .build()
        }.asJava)
        .build()
      client.scan(scanRequest).items().asScala.toSeq
    } { keyValue =>
      val getRequest = GetItemRequest.builder()
        .tableName(tableName)
        .key(Map(primaryKey -> AttributeValue.builder().s(keyValue).build()).asJava)
        .build()
      Option(client.getItem(getRequest).item()).filter(!_.isEmpty).toSeq
    }

    dynamoResult.flatMap(i => Json.fromJson[A](Json.parse(toJson(i))).asOpt).toList
  }

  private def toAttributeValueMap(json: JsValue): Map[String, AttributeValue] = {
    json.as[JsObject].fields.map {
      case (key, JsString(s)) => key -> AttributeValue.builder().s(s).build()
      case (key, JsNumber(n)) => key -> AttributeValue.builder().n(n.toString).build()
      case (key, JsBoolean(b)) => key -> AttributeValue.builder().bool(b).build()
      case (key, JsNull) => key -> AttributeValue.builder().nul(true).build()
      case (key, other) => key -> AttributeValue.builder().s(other.toString).build()
    }.toMap
  }

  private def toJson(item: java.util.Map[String, AttributeValue]): String = {
    val fields = item.asScala.map {
      case (key, av) if av.s() != null => key -> JsString(av.s())
      case (key, av) if av.n() != null => key -> JsNumber(BigDecimal(av.n()))
      case (key, av) if av.bool() != null => key -> JsBoolean(av.bool())
      case (key, av) if av.nul() != null && av.nul() => key -> JsNull
      case (key, _) => key -> JsNull
    }.toSeq
    Json.stringify(JsObject(fields))
  }
}

object JsonDynamoService {
  def forTable[A](table: String)(implicit e: ExecutionContext): JsonDynamoService[A, Future] = {
    import scalaz.std.scalaFuture._
    val dynamoDBClient = DynamoDbClient.builder()
      .credentialsProvider(CredentialsProvider)
      .region(Region.EU_WEST_1)
      .build()
    new JsonDynamoService[A, Future](table, dynamoDBClient)(futureInstance)
  }
}