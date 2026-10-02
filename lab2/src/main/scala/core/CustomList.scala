package core

import inface.ForEachCallbackInterface
import inface.SortStrategyInterface
import inface.UserTypeInterface
import inface.TraverseStrategyInterface
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.List
import java.util.Objects
import java.util.function.Predicate
import scala.jdk.CollectionConverters._

// Список блоков
class CustomList[T] {

  private val DEFAULT_BLOCK_CAPACITY: Int = 3
  private var head: CustomList.Node[T] = null
  private var tail: CustomList.Node[T] = null
  private var size: Int = 0
  private var nodeCount: Int = 0
  private var blockCapacity: Int = DEFAULT_BLOCK_CAPACITY

  def this(blockCapacity: Int) = {
    this()
    if (blockCapacity < 1) {
      throw new IllegalArgumentException("Размер массива должен быть положительным")
    }
    this.blockCapacity = blockCapacity
  }

  // Количество объектов, а не узлов
  def getSize(): Int = {
    size
  }

  def getNodeCount(): Int = {
    nodeCount
  }

  def getBlockCapacity(): Int = {
    blockCapacity
  }

  // Перегруппировка ссылок; сами объекты и их порядок не меняются
  def setBlockCapacity(capacity: Int): Unit = {
    if (capacity < 1) {
      throw new IllegalArgumentException("Размер массива должен быть положительным")
    }
    if (capacity == blockCapacity) {
      // Имитируем пустой return
    } else {
      val items = toArrayList()
      clear()
      blockCapacity = capacity
      items.asScala.foreach { item => {
        add(item)
      }}
    }
  }

  def clear(): Unit = {
    head = null
    tail = null
    size = 0
    nodeCount = 0
  }

  // Добавление ссылки в последнюю свободную ячейку; при необходимости создаётся узел
  def add(data: T): Unit = {
    if ((tail == null) || (tail.used == blockCapacity)) {
      val node = new CustomList.Node[T](blockCapacity)
      if (tail == null) {
        head = node
      } else {
        tail.next = node
      }
      tail = node
      nodeCount = nodeCount + 1
    }
    tail.values(tail.used) = data
    tail.used = tail.used + 1
    size = size + 1
  }

  private def blockAt(index: Int): CustomList.Node[T] = {
    Objects.checkIndex(index, size)
    var node = head
    var block = index / blockCapacity
    while (block > 0) {
      node = node.next
      block = block - 1
    }
    node
  }

  def get(index: Int): T = {
    blockAt(index).values(index % blockCapacity)
  }

  // Вставляет ссылку, сдвигая последующие ссылки через границы массивов
  def insert(index: Int, data: T): Unit = {
    if ((index < 0) || (index > size)) {
      throw new IndexOutOfBoundsException(java.lang.String.valueOf(index))
    }
    if (index == size) {
      add(data)
    } else {
      var node = blockAt(index)
      var offset = index % blockCapacity
      var carry = data
      
      while (node.used == blockCapacity) {
        val displaced = node.values(blockCapacity - 1)
        System.arraycopy(node.values, offset, node.values, offset + 1, blockCapacity - offset - 1)
        node.values(offset) = carry
        carry = displaced
        if (node.next == null) {
          node.next = new CustomList.Node[T](blockCapacity)
          tail = node.next
          nodeCount = nodeCount + 1
        }
        node = node.next
        offset = 0
      }
      System.arraycopy(node.values, offset, node.values, offset + 1, node.used - offset)
      node.values(offset) = carry
      node.used = node.used + 1
      size = size + 1
    }
  }

  // Удаляет ссылку и уплотняет последующие массивы
  def remove(index: Int): Unit = {
    var node = blockAt(index)
    var offset = index % blockCapacity
    var previous: CustomList.Node[T] = null
    
    if (node != head) {
      previous = head
      while (previous.next != node) {
        previous = previous.next
      }
    }
    
    var loop = true
    while (loop) {
      System.arraycopy(node.values, offset + 1, node.values, offset, node.used - offset - 1)
      if (node.next != null) {
        node.values(node.used - 1) = node.next.values(0)
        previous = node
        node = node.next
        offset = 0
      } else {
        node.used = node.used - 1
        node.values(node.used) = null.asInstanceOf[T]
        if (node.used == 0) {
          if (previous == null) {
            head = null
            tail = null
          } else {
            previous.next = null
            tail = previous
          }
          nodeCount = nodeCount - 1
        }
        loop = false
      }
    }
    size = size - 1
  }

  // Перегрузки с выбранным обходом
  def add(data: T, traversal: TraverseStrategyInterface[T]): Unit = {
    insert(size, data, traversal)
  }
  
  def get(index: Int, traversal: TraverseStrategyInterface[T]): T = {
    get(traversal.elementIndex(index, size))
  }
  
  def insert(index: Int, data: T, traversal: TraverseStrategyInterface[T]): Unit = {
    insert(traversal.insertionIndex(index, size), data)
  }
  
  def remove(index: Int, traversal: TraverseStrategyInterface[T]): Unit = {
    remove(traversal.elementIndex(index, size))
  }

  def forEach(callback: ForEachCallbackInterface[T]): Unit = {
    Objects.requireNonNull(callback)
    var node = head
    while (node != null) {
      for (i <- 0 until node.used) {
        callback.toDo(node.values(i))
      }
      node = node.next
    }
  }

  // Внутренний forEach для совместимости с лямбдами Scala
  def forEach(action: T => Unit): Unit = {
    Objects.requireNonNull(action)
    var node = head
    while (node != null) {
      for (i <- 0 until node.used) {
        action(node.values(i))
      }
      node = node.next
    }
  }

  def firstThat(predicate: Predicate[_ >: T]): T = {
    Objects.requireNonNull(predicate)
    var node = head
    var result: T = null.asInstanceOf[T]
    var found = false
    
    while ((node != null) && !found) {
      var i = 0
      while ((i < node.used) && !found) {
        if (predicate.test(node.values(i))) {
          result = node.values(i)
          found = true
        }
        i = i + 1
      }
      node = node.next
    }
    result
  }

  def firstThat(predicate: Predicate[_ >: T], traversal: TraverseStrategyInterface[T]): T = {
    val index = firstIndexThat(predicate, traversal)
    if (index < 0) {
      null.asInstanceOf[T]
    } else {
      get(index, traversal)
    }
  }

  def firstIndexThat(predicate: Predicate[_ >: T], traversal: TraverseStrategyInterface[T]): Int = {
    Objects.requireNonNull(predicate)
    Objects.requireNonNull(traversal)
    val values = toArrayList()
    var resultIdx = -1
    var found = false
    var i = 0
    
    while ((i < values.size()) && !found) {
      if (predicate.test(values.get(traversal.elementIndex(i, values.size())))) {
        resultIdx = i
        found = true
      }
      i = i + 1
    }
    resultIdx
  }

  def toArrayList(): ArrayList[T] = {
    val result = new ArrayList[T](size)
    forEach { item => {
      result.add(item)
      ()
    }}
    result
  }

  // Снимок заполненных ячеек каждого узла. Оболочки неизменяемые, объекты те же
  def getBlocks(): List[List[T]] = {
    val blocks = new ArrayList[List[T]](nodeCount)
    var node = head
    while (node != null) {
      val values = new ArrayList[T](node.used)
      for (i <- 0 until node.used) {
        values.add(node.values(i))
      }
      blocks.add(Collections.unmodifiableList(values))
      node = node.next
    }
    Collections.unmodifiableList(blocks)
  }

  def sort(strategy: SortStrategyInterface, `type`: UserTypeInterface[T]): Unit = {
    sort(strategy, `type`.getTypeComparator())
  }

  // O(N log N) + O(N) сборка блоков. Переставляются ссылки, объекты не клонируются
  def sort(strategy: SortStrategyInterface, comparator: Comparator[_ >: T]): Unit = {
    Objects.requireNonNull(strategy)
    Objects.requireNonNull(comparator)
    if (size <= 1) {
      // Имитируем пустой return
    } else {
      val items = toArrayList()
      strategy.sort(items, comparator)
      clear()
      items.asScala.foreach { item => {
        add(item)
      }}
    }
  }
}

object CustomList {
  // Вложенный статический класс Node
  private final class Node[E](capacity: Int) {
    val values: Array[E] = new Array[AnyRef](capacity).asInstanceOf[Array[E]]
    var used: Int = 0
    var next: CustomList.Node[E] = null
  }
}
