package todo.server

import java.util

import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse}
import com.twitter.finagle.Service
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpMethod
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification

class CorsFilterTest extends Specification with ShouldMatchers {
  "CorsFilter" should {
    "should add the cors headers to the request" in {
      val headers = new util.HashMap[String, String]()
      val service = new Service[FinagleRequest, FinagleResponse] {
        override def apply(request: FinagleRequest): Future[FinagleResponse] = {
          request.headerMap.foreach {
            case (key, value) => headers.put(key, value)
          }
          Future.value(null)
        }
      }

      // Given - a filter
      val request = FinagleRequest(HttpMethod.GET, "/")

      // When
      val filter = new CorsFilter
      filter(request, service)

      // Then
      headers.get("access-control-allow-origin") shouldEqual "*"
      headers.get("access-control-allow-headers") shouldEqual "accept, content-type"
      headers.get("access-control-allow-methods") shouldEqual "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH"
    }
  }
}
