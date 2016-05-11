package wiring

import play.api.mvc.Cookies
import play.api.routing.Router
import play.api.ApplicationLoader.Context
import play.api.{BuiltInComponentsFromContext, Logger, ApplicationLoader}
import play.api._


class AppLoader extends ApplicationLoader {

  def load(context: Context) = {

    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }

    new BuiltInComponentsFromContext(context) {

      lazy val map = Map[String, Router](
        "PROD" -> new AppComponents(AppComponents.PROD, this).router,
        "UAT" ->  new AppComponents(AppComponents.UAT, this).router,
        "DEV" -> new AppComponents(AppComponents.DEV, this).router
      )

      lazy val router: Router = new MultiRouter((c: Cookies) =>
        c.find(_.name == "stage").map(_.value.toUpperCase).flatMap(map.get).getOrElse(map("PROD")), map("PROD"))
    }.application
  }
}

