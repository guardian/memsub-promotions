package com.gu.memsub.services

import com.gu.aws.CredentialsProvider
import play.api.libs.json._
import scalaz.Monad

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.enhanced.dynamodb.{DynamoDbEnhancedClient, DynamoDbTable, TableSchema}
import software.amazon.awssdk.enhanced.dynamodb.Key

import scala.reflect.ClassTag

class JsonDynamoService[A, M[_]](table: DynamoDbTable[A])(implicit m: Monad[M], readsA: Reads[A], writesA: Writes[A]) {

  def all: M[Seq[A]] = Monad[M].point {
    table.scan().items().asScala.toSeq
  }

  def add(p: A): M[Unit] = Monad[M].point {
    table.putItem(p)
    ()
  }

  def findByKey(keyName: String, keyValue: String): M[Option[A]] = Monad[M].point {
    val key = Key.builder().partitionValue(keyValue).build()
    Option(table.getItem(r => r.key(key)))
  }

  def findByFilter(filterFn: A => Boolean): M[Seq[A]] = Monad[M].point {
    table.scan().items().asScala.filter(filterFn).toSeq
  }
}

object JsonDynamoService {

  def forTable[A <: AnyRef](tableName: String)(implicit classTag: ClassTag[A], ec: ExecutionContext, reads: Reads[A], writes: Writes[A]): JsonDynamoService[A, Future] = {
    import scalaz.std.scalaFuture._

    val dynamoDBClient = DynamoDbClient.builder()
      .credentialsProvider(CredentialsProvider)
      .region(Region.EU_WEST_1)
      .build()

    val enhanced = DynamoDbEnhancedClient.builder()
      .dynamoDbClient(dynamoDBClient)
      .build()

    val schema = TableSchema.fromBean(classTag.runtimeClass.asInstanceOf[Class[A]])
    val table: DynamoDbTable[A] = enhanced.table(tableName, schema)

    new JsonDynamoService[A, Future](table)
  }
}
