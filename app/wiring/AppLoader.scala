package wiring

import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, _}
import _root_.controllers.AssetsComponents
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFFilter
import play.filters.hosts.AllowedHostsFilter

class AppLoader extends ApplicationLoader {

  def load(context: Context) = {

    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }


    new BuiltInComponentsFromContext(context) with AhcWSComponents with AssetsComponents with HttpFiltersComponents {

      override def httpFilters: Seq[EssentialFilter] =
        super.httpFilters.filterNot { filter =>
          filter.getClass == classOf[AllowedHostsFilter] || filter.getClass == classOf[CSRFFilter]
        }

      lazy val applicationComponentsMap: Map[String, Router] = Map[String, Router](
        "PROD" -> new AppComponents(AppComponents.PROD, this, controllerComponents, this).router,
        "UAT" ->  new AppComponents(AppComponents.UAT, this, controllerComponents, this).router,
        "DEV" -> new AppComponents(AppComponents.DEV, this, controllerComponents, this).router
      )

      lazy val defaultRouter = applicationComponentsMap("PROD")

      lazy val router: Router = new MultiRouter((c: Cookies) =>
        c.find(_.name == "stage").map(_.value.toUpperCase).flatMap(applicationComponentsMap.get).getOrElse(defaultRouter), defaultRouter)

    }.application
  }
}

