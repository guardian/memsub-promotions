package com.gu.memsub.promo

import com.typesafe.scalalogging.StrictLogging

object LogImplicit extends StrictLogging {

  implicit class Loggable[T](t: T) {
    def withLogging(message: String): T = {
      logger.info(s"$message {$t}")
      t
    }

  }

}
