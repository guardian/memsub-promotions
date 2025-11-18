package com.gu

import software.amazon.awssdk.auth.credentials._

package object aws {
  val ProfileName = "membership"

  lazy val CredentialsProvider: AwsCredentialsProvider =
    AwsCredentialsProviderChain.builder()
      .credentialsProviders(
        ProfileCredentialsProvider.create(ProfileName),
        InstanceProfileCredentialsProvider.create(),
        EnvironmentVariableCredentialsProvider.create(),
        SystemPropertyCredentialsProvider.create()
      )
      .build()
}
