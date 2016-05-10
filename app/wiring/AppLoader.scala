package wiring
import play.api.routing.Router
import play.api.ApplicationLoader.Context
import play.api.{BuiltInComponentsFromContext, Logger, ApplicationLoader}
import com.softwaremill.macwire._
import router.Routes
import play.api._


class AppLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }

    new BuiltInComponentsFromContext(context) with AppComponents {
      val prefix: String = "/"
      lazy val router: Router = wire[Routes]
    }.application
  }
}

