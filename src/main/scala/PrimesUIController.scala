import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.Controller

import com.twitter.finatra.request.{QueryParam, RouteParam}
import com.twitter.finatra.response.Mustache

import fr.janalyse.primes._
import fr.janalyse.primesui._

case class PrimesUIContext(
  homeUrl: String,
  checkUrl: String
)



@Mustache("index")
case class IndexView(
    ctx:PrimesUIContext,
    engine:PrimesEngine,
    count:Option[Long],
    version:String,
    buildate:String,
    pversion:String
    )

case class CheckRequest(@RouteParam num:Long)



class PrimesUIController extends Controller {

  val engine = fr.janalyse.primesui.PrimesEngine.engine
  val rnd = scala.util.Random
  def nextInt = rnd.nextInt(10000)

  val ctx = PrimesUIContext("/", "/check/")
  
  // -------------------------------------------------------------------------------------------------
  get("/") { request: Request =>
    val count = if (!engine.useSession) None else {
      Some(9999L)
    }
    IndexView(
        ctx = ctx,
        engine = engine,
        count = count,
        version = MetaInfo.version,
        buildate = fr.janalyse.primesui.MetaInfo.buildate,
        pversion = primes.MetaInfo.version
        )
  }

  // -------------------------------------------------------------------------------------------------  
  def check(num:Long) = {
    val state = engine.check(num)
    val resp = state match {
      case Some(r) if r.isPrime =>
        s"<html><body>$num is the ${r.nth} prime number</body></html>"
      case Some(r) =>
        s"<html><body>$num is the ${r.nth} not prime number</body></html>"
      case None =>
        s"<html><body>$num primes state is unknown</body></html>"
    }
    resp    
  }
  
  get("/check") { request: Request => check(nextInt)}
  get("/check/:num") { request: CheckRequest => check(request.num)}
 
  get("/js/:file") { request: Request => response.ok.file("/static/js/"+request.params("file")) }
  get("/css/:file") { request: Request => response.ok.file("/static/css/"+request.params("file")) }
  get("/images/:file") { request: Request => response.ok.file("/static/images/"+request.params("file")) }
  
}