package todo.model

import java.util.UUID

case class TodoItem(id: String,
                    title: String,
                    url: String,
                    completed: Boolean,
                    order: Int,
                    text: String)

object TodoItem {
  def random(): TodoItem = {
    TodoItem(
      id = UUID.randomUUID().toString,
      title = UUID.randomUUID().toString,
      url = UUID.randomUUID().toString,
      completed = false,
      order = 123,
      text = UUID.randomUUID().toString
    )
  }
}
