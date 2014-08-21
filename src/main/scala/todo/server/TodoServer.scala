package todo.server

import java.nio.charset.Charset

import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse}
import com.twitter.finagle.{Filter, Service}
import com.twitter.finatra.FinatraServer
import com.twitter.util.Future
import org.jboss.netty.buffer.ChannelBuffer
import todo.api.DefaultTodoApi
import todo.controller.TodoController
import scala.collection.JavaConversions._

class CorsFilter extends Filter[FinagleRequest, FinagleResponse, FinagleRequest, FinagleResponse] {
  override def apply(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]): Future[FinagleResponse] = {
    service(request).map {
      response =>
        response
          .headers()
          .add("access-control-allow-origin", "*")
          .add("access-control-allow-headers", "accept, content-type")
          .add("access-control-allow-methods", "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH")
        response
    }
  }
}

class LoggingFilter extends Filter[FinagleRequest, FinagleResponse, FinagleRequest, FinagleResponse] {
  override def apply(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]): Future[FinagleResponse] = {
    // REQUEST
    println("# Request ---------------------------------------------------------------")
    println(s"${request.getMethod()} ${request.getUri()}")
    request.headers().entries().map {
      case entry => println(f"${entry.getKey}%30s: ${entry.getValue}")
    }
    println("")
    println(toString(request.getContent()))

    // SERVICE
    val future: Future[FinagleResponse] = service(request)

    // RESPONSE
    println("# Response --------------------------------------------------------------")
    future.map {
      response => {
        response.headerMap.map {
          case (key, value) => println(f"$key%30s: $value")
        }

        println("")
        println(toString(response.getContent()))
      }
    }

    future
  }

  def toString(content: ChannelBuffer): String = {
    if (content != null) {
      content.copy().toString(Charset.defaultCharset())
    } else {
      ""
    }
  }
}

class TodoServer extends FinatraServer {
  addFilter(new CorsFilter)
  addFilter(new LoggingFilter)

  val controller = new TodoController(new DefaultTodoApi)
  register(controller)
}

object Server extends App {
  System.setProperty("com.twitter.finatra.config.port", s":${System.getenv("PORT")}")
  System.setProperty("com.twitter.finatra.config.adminPort", "")

  val server = new TodoServer
  server.start()
}