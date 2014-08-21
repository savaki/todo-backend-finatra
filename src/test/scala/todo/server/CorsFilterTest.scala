package todo.server

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse}
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http.HttpHeaders
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification

class CorsFilterTest extends Specification with ShouldMatchers {
  "CorsFilter" should {
    "should add the cors headers to the request" in {
      val service = new Service[FinagleRequest, FinagleResponse] {
        override def apply(request: FinagleRequest): Future[FinagleResponse] = {
          Future.value(FinagleResponse())
        }
      }

      // When
      val filter = new CorsFilter
      val response: FinagleResponse = Await.result(filter(FinagleRequest("/"), service))

      // Then
      val headers: HttpHeaders = response.headers()
      headers.get("access-control-allow-origin") shouldEqual "*"
      headers.get("access-control-allow-headers") shouldEqual "accept, content-type"
      headers.get("access-control-allow-methods") shouldEqual "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH"
    }
  }
}
