import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.{ QueryParam, RouteParam }
import com.twitter.finatra.response.Mustache
import fr.janalyse.primes._
import fr.janalyse.primesui._
import com.twitter.finatra.http.request.RequestUtils

@Mustache("index")
case class IndexView(
  ctx: PrimesUIContext,
  primesCount: Long,
  valuesCount: Long,
  useCache: Boolean,
  highestPrime: Option[Long],
  hitCount: Option[Long],
  version: String,
  buildate: String,
  pversion: String)

@Mustache("check")
case class CheckView(
  ctx: PrimesUIContext,
  num: Long,
  value: Option[CheckedValue[Long]],
  again: Option[String])

@Mustache("prime")
case class PrimeView(
  ctx: PrimesUIContext,
  nth: Long,
  result: Option[CheckedValue[Long]],
  again: Option[String])


case class CheckRequest(
  @RouteParam num: Long)

case class PrimeRequest(
  @RouteParam nth: Long)


case class PrimesUIContext(
  base: String,
  check: String = "check",
  prime: String = "prime",
  goodlogscheck: String = "goodlogscheck"
  )


class PrimesUIController extends Controller {

  lazy val engine = fr.janalyse.primesui.PrimesEngine.engine
  val rnd = scala.util.Random
  def nextInt = rnd.nextInt(10000)

  val ctx = PrimesUIContext("/")
  import ctx.base

  def dumpInfo(rq: Request) {
    info("path=" + rq.path)
    info("uri=" + rq.uri)
    info("loc=" + rq.location)
    info("ref=" + rq.referer)
    info("agent=" + rq.userAgent)
    info("host=" + rq.host)
    info("pathUrl=" + RequestUtils.pathUrl(rq))
  }

  // -------------------------------------------------------------------------------------------------
  def home(request: Request) = {
    val hitCount = if (!engine.useSession) None else {
      Some(9999L)
    }
    //dumpInfo(request)
    IndexView(
      ctx = ctx,
      highestPrime = engine.lastPrime.map(_.value),
      primesCount = engine.primesCount(),
      valuesCount = engine.valuesCount(),
      hitCount = hitCount,
      useCache = engine.isUseCache(),
      version = MetaInfo.version,
      buildate = fr.janalyse.primesui.MetaInfo.buildate,
      pversion = primes.MetaInfo.version)
  }

  get(ctx.base) { request: Request => home(request) }
  
  // -------------------------------------------------------------------------------------------------  
  def check(num: Long, again: Option[String]) = {
    val state = engine.check(num)
    CheckView(ctx, num, state, again)
  }

  get(base + ctx.check) { request: Request => check(nextInt, Some(ctx.check))}
  get(base + ctx.check + "/:num") { request: CheckRequest => check(request.num, None)}

  // -------------------------------------------------------------------------------------------------
  
  def durationOf[T](proc: => T):Tuple2[T,Long] = {
    val started=System.currentTimeMillis()
    val state:T = proc
    val duration=(System.currentTimeMillis()-started)
    (state, duration)
  }
  
  def goodlogscheck(num: Long, again: Option[String]) = {
    info("Checking#"+num)
    val (state, duration) = durationOf {
      engine.check(num)
    }
    def message="Checked "+state.toString()+" in "+duration+" milliseconds"
    debug(message)
    info(message)
    CheckView(ctx, num, state, again)
  }

  get(base + ctx.goodlogscheck) { request: Request => goodlogscheck(nextInt, Some(ctx.goodlogscheck))}
  get(base + ctx.goodlogscheck + "/:num") { request: CheckRequest => goodlogscheck(request.num, None)}

  // -------------------------------------------------------------------------------------------------  
  def prime(nth: Long, again: Option[String]) = {
    val result = engine.getPrime(nth)
    PrimeView(ctx, nth, result, again)
  }

  get(base + ctx.prime) { request: Request => prime(nextInt, Some(ctx.prime)) }
  get(base + ctx.prime + "/:nth") { request: PrimeRequest => prime(request.nth, None)}
  
  // -------------------------------------------------------------------------------------------------  
  for { res <- List("js", "css", "images") } {
    get(s"$base$res/:*") { request: Request =>
      response.ok.file(s"/static/$res/" + request.params("*"))
    }
  }
}
