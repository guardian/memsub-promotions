package com.gu.memsub.services

import com.gu.aws.CredentialsProvider
import play.api.libs.json._
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scalaz.Monad

class JsonDynamoService[A, M[_]](tableName: String, client: DynamoDbClient)(implicit m: Monad[M]) extends StrictLogging {

  def all(implicit formatter: Reads[A]): M[Seq[A]] = Monad[M].point {
    /**
     * Due to the way the promos data is modelled we have to perform a full scan of the table.
     * This means we need to paginate, keeping track of the lastEvaluatedKey after each batch.
     */
    @scala.annotation.tailrec
    def scan(
      accumulatedItems: List[java.util.Map[String, AttributeValue]],
      lastKey: Option[java.util.Map[String, AttributeValue]]
    ): List[java.util.Map[String, AttributeValue]] = {
      val scanRequestBuilder = ScanRequest.builder().tableName(tableName)
      lastKey.foreach(scanRequestBuilder.exclusiveStartKey)
      val scanResponse = client.scan(scanRequestBuilder.build())

      val newItems = accumulatedItems ++ scanResponse.items().asScala.toList
      // If lastEvaluatedKey is empty then no more results remaining
      val nextKey = Option(scanResponse.lastEvaluatedKey()).filter(!_.isEmpty)

      if (nextKey.isDefined) scan(newItems, nextKey)
      else newItems
    }

    val allItems = scan(List.empty, None)
    logger.info(s"Got ${allItems.length} items from Dynamo for table $tableName")
    val jsonItems = allItems.map(i => Json.fromJson[A](dynamoMapToJson(i)))
      .flatMap {
        case JsSuccess(value, _) => Some(value)
        case JsError(errors) =>
          logger.error(s"Error reading DynamoDb data: $errors")
          None
      }

    logger.info(s"Serialized ${jsonItems.length} items.")
    jsonItems
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

  // Transform Play JSON value to DynamoDb attribute value map
  private def toAttributeValueMap(json: JsValue): Map[String, AttributeValue] = {
    def jsonToAttributeValue(json: JsValue): AttributeValue = {
      json match {
        case JsNull =>
          AttributeValue.builder().nul(true).build()
        case JsBoolean(b) =>
          AttributeValue.builder().bool(b).build()
        case JsNumber(n) =>
          AttributeValue.builder().n(n.toString).build()
        case JsString(s) =>
          AttributeValue.builder().s(s).build()
        case JsArray(arr) =>
          AttributeValue.builder().l(arr.map(jsonToAttributeValue).asJava).build()
        case obj: JsObject =>
          val map = obj.fields.map {
            case (k, v) => k -> jsonToAttributeValue(v)
          }.toMap
          AttributeValue.builder().m(map.asJava).build()
      }
    }

    json.as[JsObject].fields.map {
      case (key, value) => key -> jsonToAttributeValue(value)
    }.toMap
  }

  // Transform DynamoDb attribute value map to Play JSON value
  private def dynamoMapToJson(item: java.util.Map[String, AttributeValue]): JsObject = {
    def dynamoToJson(attribute: AttributeValue): JsValue = {
      if (attribute.hasM()) {
        // Map
        dynamoMapToJson(attribute.m())
      } else if (attribute.hasL()) {
        // List (array)
        JsArray(attribute.l().asScala.map(dynamoToJson).toSeq)
      } else if (attribute.hasSs()) {
        // String set
        JsArray(attribute.ss().asScala.map(JsString(_)).toSeq)
      } else if (attribute.hasNs()) {
        // Number set
        JsArray(attribute.ns().asScala.map(n => JsNumber(BigDecimal(n))).toSeq)
      } else if (attribute.s() != null) {
        // String
        JsString(attribute.s())
      } else if (attribute.n() != null) {
        // Number
        JsNumber(BigDecimal(attribute.n()))
      } else if (attribute.bool() != null) {
        // Boolean
        JsBoolean(attribute.bool())
      } else if (attribute.nul() != null && attribute.nul()) {
        // Null
        JsNull
      } else {
        JsNull
      }
    }

    JsObject(item.asScala.view.mapValues(dynamoToJson).toMap)
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