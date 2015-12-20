import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

import com.twitter.finatra.request.{QueryParam, RouteParam}
case class CheckRequest(@RouteParam num:Long)


class PrimesUIController extends Controller {

  val engine = fr.janalyse.primesui.PrimesEngine.engine
  val rnd = scala.util.Random
  def nextInt = rnd.nextInt(10000)

  get("/") { request: Request =>
    "<html><body><h1>PrimesUI</h1></body></html>"
  }

  
  get("/check") { request: Request =>
    val num = nextInt
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
  
  
  get("/check/:num") { request: CheckRequest =>
    val num = request.num
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

}