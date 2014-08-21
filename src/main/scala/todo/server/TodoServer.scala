package todo.server

import com.twitter.finatra.FinatraServer
import todo.api.DefaultTodoApi
import todo.controller.TodoController

class TodoServer extends FinatraServer {
  val controller = new TodoController(new DefaultTodoApi)
  register(controller)
}

object Server extends App {
  System.setProperty("com.twitter.finatra.config.port", s":${System.getenv("PORT")}")
  System.setProperty("com.twitter.finatra.config.adminPort", "")

  val server = new TodoServer
  server.start()
}