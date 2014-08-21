package todo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.finatra.Controller
import todo.api.TodoApi
import todo.model.TodoItem

class TodoController(service: TodoApi) extends Controller {
  private[this] val jsonMapper = {
    val m = new ObjectMapper()
    m.registerModule(DefaultScalaModule)
  }

  val ok = render.body("").toFuture

  options("/todos") {
    request => ok
  }

  options("/todos/:id") {
    request => ok
  }

  get("/todos") {
    request => render.json(service.findAll()).toFuture
  }

  get("/todos/:id") {
    request =>
      val id = request.routeParams("id")
      render.json(service.find(id)).toFuture
  }

  post("/todos") {
    request =>
      val template: TodoItem = jsonMapper.readValue(request.getContentString(), classOf[TodoItem])
      val item: TodoItem = service.create(template, TodoController.fqdn)
      render.header("Location", s"/todos/${item.id}").json(item).status(201).toFuture
  }

  patch("/todos/:id") {
    request =>
      val id = request.routeParams("id")
      val template: TodoItem = jsonMapper.readValue(request.getContentString(), classOf[TodoItem])
      val item: TodoItem = service.update(id, template).get
      render.json(item).toFuture
  }

  delete("/todos") {
    request =>
      service.deleteAll()
      ok
  }

  delete("/todos/:id") {
    request =>
      val id = request.routeParams("id")
      service.delete(id)
      ok
  }
}

object TodoController {
  val fqdn = (path: String) => s"http://todo-backend-finatra.herokuapp.com/todos/$path"
}