package com.gu.aws

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.{S3Client}
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, S3Exception}
import com.gu.monitoring.SafeLogger
import com.gu.monitoring.SafeLogger._
import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json.{JsValue, Json}
import scalaz.{-\/, \/, \/-}
import scala.io.Source
import scala.util.{Failure, Success, Try}

object AwsS3 extends StrictLogging {
  lazy val client: S3Client =
    S3Client.builder()
      .region(Region.EU_WEST_1)
      .credentialsProvider(CredentialsProvider)
      .build()

  def fetchObject(s3Client: S3Client, request: GetObjectRequest): Try[java.io.InputStream] = Try {
      s3Client.getObject(request)
  }

  def fetchJson(s3Client: S3Client, request: GetObjectRequest): String \/ JsValue = {
    logger.info(s"Getting file from S3. Bucket: ${request.bucket()} | Key: ${request.key()}")

    val attempt = for {
      s3Stream <- fetchObject(s3Client, request)
      json <- Try(Json.parse(Source.fromInputStream(s3Stream).mkString))
      _ <- Try(s3Stream.close())
    } yield json

    attempt match {
      case Success(json) =>
        logger.info(s"Successfully loaded ${request.key()} from ${request.bucket()}")
        \/-(json)

      case Failure(ex: S3Exception) =>
        SafeLogger.error(scrub"S3 error while loading ${request.key()} from ${request.bucket()}", ex)
        -\/(s"S3 error: ${ex.awsErrorDetails().errorMessage()}")

      case Failure(ex) =>
        SafeLogger.error(scrub"Failed to load JSON from S3 bucket ${request.bucket()}", ex)
        -\/(s"Failed to load JSON due to $ex")
    }
  }
}
