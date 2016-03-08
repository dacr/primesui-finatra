import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.Controller

import com.twitter.finatra.request.{ QueryParam, RouteParam }
import com.twitter.finatra.response.Mustache

import fr.janalyse.primes._
import fr.janalyse.primesui._

case class PrimesUIContext(
  base: String,
  checkUrl: String)

@Mustache("index")
case class IndexView(
  ctx: PrimesUIContext,
  engine: PrimesEngine,
  highestPrime: Option[Long],
  count: Option[Long],
  version: String,
  buildate: String,
  pversion: String)

@Mustache("check")
case class CheckView(
  ctx:PrimesUIContext,
  num:Long,
  value:Option[CheckedValue[Long]],
  againUrl:Option[String]
)


case class CheckRequest(
  @RouteParam num: Long)

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
      highestPrime = engine.lastPrime.map(_.value),
      version = MetaInfo.version,
      buildate = fr.janalyse.primesui.MetaInfo.buildate,
      pversion = primes.MetaInfo.version)
  }

  // -------------------------------------------------------------------------------------------------  
  def check(num: Long, againUrl:Option[String]) = {
    val state = engine.check(num)
    CheckView(ctx, num, state, againUrl)
  }

  get("/check") { request: Request => check(nextInt, Some("check")) }
  get("/check/:num") { request: CheckRequest => check(request.num, None) }

  // -------------------------------------------------------------------------------------------------  
  for { res <- List("js", "css", "images") } {
    get(s"/$res/:file") { request: Request => response.ok.file(s"/static/$res/" + request.params("file")) }
  }
}