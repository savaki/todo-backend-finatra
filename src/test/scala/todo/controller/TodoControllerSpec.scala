package todo.controller

import com.twitter.finatra.test.{MockApp, MockResult}
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.scalatest.mock.MockitoSugar
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import todo.api.TodoApi
import todo.model.TodoItem
import org.mockito.Mockito._

class TodoControllerSpec extends Specification with ShouldMatchers with MockitoSugar {
  val anItem: TodoItem = TodoItem.random(TodoController.fqdn)

  val updatedItem = anItem.copy(url = "http://www.google.com")

  def service: TodoApi = {
    val api: TodoApi = mock[TodoApi]
    when(api.findAll()) thenReturn Array(anItem)
    when(api.find(anItem.id)) thenReturn Option(anItem)
    when(api.create(anItem, TodoController.fqdn)) thenReturn anItem
    when(api.update(anItem.id, updatedItem)) thenReturn Option(updatedItem)
    api
  }

  def app: MockApp = {
    MockApp(new TodoController(service))
  }

  "todo controller" should {
    "handle OPTION" in {
      "return ok for OPTION /todos" in {
        app.options("/todos").status shouldEqual HttpResponseStatus.OK
      }

      "return ok for OPTION /todos/1234" in {
        app.options("/todos/1234").status shouldEqual HttpResponseStatus.OK
      }
    }

    "handle fetch via GET" in {
      "retrieve all todo items for GET /todos" in {
        val result: MockResult = app.get("/todos")
        val items: Array[TodoItem] = MockApp.mapper.readValue(result.body, classOf[Array[TodoItem]])
        items shouldEqual Array(anItem)
      }

      "retrieve a specific item for GET /todos/:id" in {
        val result: MockResult = app.get(s"/todos/${anItem.id}")
        val item: TodoItem = MockApp.mapper.readValue(result.body, classOf[TodoItem])
        item shouldEqual anItem
      }
    }

    "create and update via POST and PATCH" in {
      "create a new item via POST /todos" in {
        val result: MockResult = app.post("/todos", body = anItem)
        result.getHeader("Location") shouldEqual s"/todos/${anItem.id}"
      }

      "create a new item via POST /todos returns item" in {
        val result: MockResult = app.post("/todos", body = anItem)
        val item: TodoItem = MockApp.mapper.readValue(result.body, classOf[TodoItem])
        item shouldEqual anItem
      }

      "create a new item via POST /todos returns a fully qualified url" in {
        val result: MockResult = app.post("/todos", body = anItem)
        val item: TodoItem = MockApp.mapper.readValue(result.body, classOf[TodoItem])
        item.url shouldEqual TodoController.fqdn(item.id)
      }

      "update an existing item via PATCH /todos/:id" in {
        val result: MockResult = app.patch(s"/todos/${anItem.id}", body = updatedItem)
        val item: TodoItem = MockApp.mapper.readValue(result.body, classOf[TodoItem])
        item shouldEqual updatedItem
      }
    }

    "delete items via DELETE" in {
      "delete all items via DELETE /todos" in {
        app.delete("/todos").status shouldEqual HttpResponseStatus.OK
      }

      "delete an item via DELETE /todos/:id" in {
        app.delete("/todos/1234").status shouldEqual HttpResponseStatus.OK
      }
    }
  }
}
