package com.gu.memsub.auth.common

import software.amazon.awssdk.services.s3.model.GetObjectRequest
import com.google.auth.oauth2.ServiceAccountCredentials
import com.gu.aws.AwsS3
import com.gu.googleauth.{AntiForgeryChecker, GoogleAuthConfig, GoogleGroupChecker}
import com.typesafe.config.Config
import play.api.http.HttpConfiguration

object MemSub {

  object Google {
    val GuardianAppsDomain = "guardian.co.uk"

    def googleAuthConfigFor(config: Config, httpConfiguration: HttpConfiguration): GoogleAuthConfig = {
      val c = config.getConfig("google.oauth")
      GoogleAuthConfig(
        c.getString("client.id"),
        c.getString("client.secret"),
        c.getString("callback"),
        List(GuardianAppsDomain), // Google App domain to restrict login
        antiForgeryChecker = AntiForgeryChecker.borrowSettingsFromPlay(httpConfiguration)
      )
    }

    def googleGroupCheckerFor(config: Config): GoogleGroupChecker = {
      val request = GetObjectRequest.builder()
        .bucket("membership-private")
        .key("google-auth-service-account-certificate.json")
        .build()

      AwsS3.fetchObject(AwsS3.client, request).map { stream =>
        val googleServiceAccountCredential = ServiceAccountCredentials.fromStream(stream)
        stream.close()

        val impersonatedUser = config.getString("google.oauth.impersonatedUser")

        new GoogleGroupChecker(
          impersonatedUser,
          googleServiceAccountCredential
        )
      }.get // created on startup
    }
  }

}
