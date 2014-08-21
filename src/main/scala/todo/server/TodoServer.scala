package todo.server

import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse}
import com.twitter.finagle.{Filter, Service}
import com.twitter.finatra.FinatraServer
import com.twitter.util.Future
import todo.api.DefaultTodoApi
import todo.controller.TodoController

class CorsFilter extends Filter[FinagleRequest, FinagleResponse, FinagleRequest, FinagleResponse] {
  override def apply(request: FinagleRequest, service: Service[FinagleRequest, FinagleResponse]): Future[FinagleResponse] = {
    request
      .headers()
      .add("access-control-allow-origin", "*")
      .add("access-control-allow-headers", "accept, content-type")
      .add("access-control-allow-methods", "GET,HEAD,POST,DELETE,OPTIONS,PUT,PATCH")
    service(request)
  }
}

class TodoServer extends FinatraServer {
  addFilter(new CorsFilter)

  val controller = new TodoController(new DefaultTodoApi)
  register(controller)
}

object Server extends App {
  System.setProperty("com.twitter.finatra.config.port", s":${System.getenv("PORT")}")
  System.setProperty("com.twitter.finatra.config.adminPort", "")

  val server = new TodoServer
  server.start()
}