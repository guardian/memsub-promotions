package wiring

import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader, LoggerConfigurator}

class AppLoader extends ApplicationLoader {

  def load(context: Context) = {

    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new AppComponents(context).application
  }
}

