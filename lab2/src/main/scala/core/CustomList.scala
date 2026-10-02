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

class CustomList[T] {

  private val DEFAULT_BLOCK_CAPACITY: Int = 3
  private var head: CustomList.Node[T] = null
  private var tail: CustomList.Node[T] = null
  private var size: Int = 0
  private var nodeCount: Int = 0
  private var blockCapacity: Int = DEFAULT_BLOCK_CAPACITY

  // Кэш для оптимизации частых обращений к соседним элементам по индексу (сортировка)
  @transient private var cachedNode: CustomList.Node[T] = null
  @transient private var cachedBlockIndex: Int = -1

  def this(blockCapacity: Int) = {
    this()
    if (blockCapacity < 1) {
      throw new IllegalArgumentException("Размер массива должен быть положительным")
    }
    this.blockCapacity = blockCapacity
  }

  def getSize(): Int = size
  def getNodeCount(): Int = nodeCount
  def getBlockCapacity(): Int = blockCapacity

  private def invalidateCache(): Unit = {
    cachedNode = null
    cachedBlockIndex = -1
  }

  def setBlockCapacity(capacity: Int): Unit = {
    if (capacity < 1) {
      throw new IllegalArgumentException("Размер массива должен быть положительным")
    }
    if (capacity == blockCapacity) {
      // Ничего не делаем
    } else {
      val items = toArrayList()
      clear()
      blockCapacity = capacity
      items.asScala.foreach { item => add(item) }
    }
  }

  def clear(): Unit = {
    head = null
    tail = null
    size = 0
    nodeCount = 0
    invalidateCache()
  }

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
    invalidateCache()
  }

  // Оптимизированный blockAt с кэшированием последнего запрошенного узла
  private def blockAt(index: Int): CustomList.Node[T] = {
    Objects.checkIndex(index, size)
    val targetBlockIndex = index / blockCapacity

    if (cachedNode != null && cachedBlockIndex == targetBlockIndex) {
      return cachedNode
    }

    var node: CustomList.Node[T] = null
    var startBlock = 0

    if (cachedNode != null && targetBlockIndex >= cachedBlockIndex) {
      node = cachedNode
      startBlock = cachedBlockIndex
    } else {
      node = head
      startBlock = 0
    }

    var block = startBlock
    while (block < targetBlockIndex) {
      node = node.next
      block = block + 1
    }

    cachedNode = node
    cachedBlockIndex = targetBlockIndex
    node
  }

  def get(index: Int): T = {
    blockAt(index).values(index % blockCapacity)
  }

  // Новый метод inplace-модификации значения по физическому индексу
  def set(index: Int, data: T): Unit = {
    blockAt(index).values(index % blockCapacity) = data
  }

  // Новый метод быстрого inplace-обмена двух ячеек за O(1) переходов по кэшу
  def swap(index1: Int, index2: Int): Unit = {
    if (index1 == index2) return
    val node1 = blockAt(index1)
    val offset1 = index1 % blockCapacity
    val temp = node1.values(offset1)

    val node2 = blockAt(index2)
    val offset2 = index2 % blockCapacity

    node1.values(offset1) = node2.values(offset2)
    node2.values(offset2) = temp
  }

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
      invalidateCache()
    }
  }

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
    invalidateCache()
  }

  def add(data: T, traversal: TraverseStrategyInterface[T]): Unit = insert(size, data, traversal)
  def get(index: Int, traversal: TraverseStrategyInterface[T]): T = get(traversal.elementIndex(index, size))
  def insert(index: Int, data: T, traversal: TraverseStrategyInterface[T]): Unit = insert(traversal.insertionIndex(index, size), data)
  def remove(index: Int, traversal: TraverseStrategyInterface[T]): Unit = remove(traversal.elementIndex(index, size))

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
    if (index < 0) null.asInstanceOf[T] else get(index, traversal)
  }

  // Оптимизированный поиск индекса БЕЗ создания промежуточного ArrayList
  def firstIndexThat(predicate: Predicate[_ >: T], traversal: TraverseStrategyInterface[T]): Int = {
    Objects.requireNonNull(predicate)
    Objects.requireNonNull(traversal)
    var resultIdx = -1
    var found = false
    var i = 0
    
    while ((i < size) && !found) {
      if (predicate.test(get(traversal.elementIndex(i, size)))) {
        resultIdx = i
        found = true
      }
      i = i + 1
    }
    resultIdx
  }

  def toArrayList(): ArrayList[T] = {
    val result = new ArrayList[T](size)
    forEach { item => 
      result.add(item)
      ()
    }
    result
  }

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

  // Теперь никакой сборки/разборки через ArrayList! Изменения вносятся inplace.
  def sort(strategy: SortStrategyInterface, comparator: Comparator[_ >: T]): Unit = {
    Objects.requireNonNull(strategy)
    Objects.requireNonNull(comparator)
    if (size <= 1) {
      // Имитируем пустой return
    } else {
      strategy.sort(this, comparator)
      invalidateCache() // Сбрасываем кэш после хаотичных перемещений при сортировке
    }
  }
}

object CustomList {
  private final class Node[E](capacity: Int) {
    val values: Array[E] = new Array[AnyRef](capacity).asInstanceOf[Array[E]]
    var used: Int = 0
    var next: CustomList.Node[E] = null
  }
}
