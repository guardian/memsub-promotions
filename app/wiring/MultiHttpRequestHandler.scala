package wiring
import play.api.http.{DefaultHttpRequestHandler, HttpConfiguration, HttpErrorHandler, HttpFilters}
import play.api.mvc.{Cookies, EssentialFilter, Handler, RequestHeader}
import play.api.routing.Router

class MultiHttpRequestHandler(
  errorHandler: HttpErrorHandler,
  configuration: HttpConfiguration,
  filters: Seq[EssentialFilter],
  router: Cookies => Router,
  default: Router
) extends DefaultHttpRequestHandler(
  default,
  errorHandler,
  configuration,
  filters: _*
) {

  override def routeRequest(request: RequestHeader): Option[Handler] =
    router(request.cookies).routes.lift(request)

}
