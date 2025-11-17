package com.gu.memsub.services

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import scala.jdk.CollectionConverters._

class JsonDynamoServiceSpec extends PlaySpec {
  "JsonDynamoService" should {
    "roud-trip JSON to DynamoDb format" in {
      val originalJson = Json.obj(
        "id" -> "abc123",
        "name" -> "Test User",
        "age" -> 25,
        "active" -> true,
        "tags" -> Json.arr("tag1", "tag2"),
        "metadata" -> Json.obj("key" -> "value")
      )

      val dynamoMap = JsonDynamoService.toAttributeValueMap(originalJson)
      val resultJson = JsonDynamoService.dynamoMapToJson(dynamoMap.asJava)

      resultJson mustBe originalJson
    }
  }
}
