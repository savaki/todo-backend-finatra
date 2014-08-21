package todo.api

import java.util
import java.util.{Collections, UUID}

import todo.model.TodoItem

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

trait TodoApi {
  def delete(id: String)

  def deleteAll()

  def update(id: String, item: TodoItem): Option[TodoItem]

  def create(item: TodoItem, fqdn: String => String): TodoItem

  def find(id: String): Option[TodoItem]

  def findAll(): Array[TodoItem]
}

class DefaultTodoApi extends TodoApi {
  private[this] val items = Collections.synchronizedList(new util.ArrayList[TodoItem]())

  def delete(id: String): Unit = {
    find(id).map(item => items.remove(item))
  }

  def deleteAll(): Unit = {
    items.clear()
  }

  def update(id: String, item: TodoItem): Option[TodoItem] = {
    find(id).map {
      original => original.copy(
        title = Option(item.title).getOrElse(original.title),
        url = Option(item.url).getOrElse(original.url),
        completed = item.completed,
        order = item.order,
        text = Option(item.text).getOrElse(original.text)
      )
    }
  }

  def findAll(): Array[TodoItem] = {
    items.asScala.toArray
  }

  def find(id: String): Option[TodoItem] = {
    items.find(item => item.id == id)
  }

  def create(template: TodoItem, fqdn: String => String): TodoItem = {
    val theId = UUID.randomUUID().toString
    val theUrl = fqdn(theId)
    val item: TodoItem = template.copy(id = theId, url = theUrl)
    items.add(item)
    item
  }
}