package todo.model

import java.util.UUID

case class TodoItem(id: String,
                    title: String,
                    url: String,
                    completed: Boolean,
                    order: Int,
                    text: String)

object TodoItem {
  def random(fqdn: String => String): TodoItem = {
    val theId: String = UUID.randomUUID().toString
    TodoItem(
      id = theId,
      title = UUID.randomUUID().toString,
      url = fqdn(theId),
      completed = false,
      order = 123,
      text = UUID.randomUUID().toString
    )
  }
}
