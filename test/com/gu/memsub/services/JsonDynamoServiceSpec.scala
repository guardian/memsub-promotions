package com.gu.memsub.services

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import scala.jdk.CollectionConverters._

class JsonDynamoServiceSpec extends PlaySpec {
  "JsonDynamoService" should {
    "roud-trip JSON to DynamoDb format" in {
      val originalJson = Json.obj(
        "string" -> "abc123",
        "number" -> 25,
        "bool" -> true,
        "array" -> Json.arr("a", "b"),
        "obj" -> Json.obj(
          "key1" -> Json.arr("c", "d"),
          "key2" -> Json.obj("key" -> "nested"),
        )
      )

      val dynamoMap = JsonDynamoService.toAttributeValueMap(originalJson)
      val resultJson = JsonDynamoService.dynamoMapToJson(dynamoMap.asJava)

      resultJson mustBe originalJson
    }
  }
}
