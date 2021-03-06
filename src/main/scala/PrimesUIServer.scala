import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.filters._



object PrimesUIServerMain extends PrimesUIServer {
}

class PrimesUIServer extends HttpServer {

  override def configureHttp(router: HttpRouter) {
    router
      .filter[LoggingMDCFilter[Request, Response]] // FinagleMDCInitializer.init
      .filter[TraceIdMDCFilter[Request, Response]] // Inject "traceId" in the MDC context
      .filter[CommonFilters]
      .filter[HttpResponseFilter[Request]]
      .filter[ExceptionMappingFilter[Request]]
      .add[PrimesUIController]
  }
}
