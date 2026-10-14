package core

import inface.ForEachCallbackInterface
import inface.SortStrategyInterface
import inface.TraverseStrategyInterface
import inface.UserTypeInterface
import java.util.ArrayList
import java.util.Collections

class CustomList<T>(blockCapacity: Int = DEFAULT_BLOCK_CAPACITY) {

    private class Node<E>(capacity: Int) {
        // В Kotlin массивы инвариантны, поэтому создаем массив Any? и приводим типы при извлечении
        val values: Array<Any?> = arrayOfNulls(capacity)
        var used: Int = 0
        var next: Node<E>? = null
    }

    companion object {
        const val DEFAULT_BLOCK_CAPACITY: Int = 3
    }

    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    // Публичные read-only свойства вместо Java-геттеров getSize(), getNodeCount(), getBlockCapacity()
    var size: Int = 0
        private set

    var nodeCount: Int = 0
        private set

    var blockCapacity: Int = blockCapacity
        set(value) {
            if (value < 1) throw IllegalArgumentException("Размер массива должен быть положительным")
            if (value == field) return
            val items = toArrayList()
            clear()
            field = value
            items.forEach(this::add)
        }

    // Кэш для оптимизации blockAt при частых последовательных запросах (сортировка)
    private var cachedNode: Node<T>? = null
    private var cachedBlockIndex: Int = -1

    init {
        if (blockCapacity < 1) throw IllegalArgumentException("Размер массива должен быть положительным")
    }

    private fun invalidateCache() {
        cachedNode = null
        cachedBlockIndex = -1
    }

    public fun clear() {
        head = null
        tail = null
        size = 0
        nodeCount = 0
        invalidateCache()
    }

    public fun add(data: T) {
        var currentTail = tail
        if (currentTail == null || currentTail.used == blockCapacity) {
            val node = Node<T>(blockCapacity)
            if (currentTail == null) {
                head = node
            } else {
                currentTail.next = node
            }
            tail = node
            currentTail = node
            nodeCount++
        }
        currentTail.values[currentTail.used++] = data
        size++
        invalidateCache()
    }

    // Оптимизированный поиск блока с кэшированием позиции
    private fun blockAt(index: Int): Node<T> {
        if (index !in 0 until size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        val targetBlockIndex = index / blockCapacity

        // Если запрашивается тот же блок, что и в прошлый раз
        val currentCachedNode = cachedNode
        if (currentCachedNode != null && cachedBlockIndex == targetBlockIndex) {
            return currentCachedNode
        }

        var node: Node<T>?
        val startBlock: Int

        // Определяем, откуда эффективнее начать: от head или от кэша
        if (currentCachedNode != null && targetBlockIndex >= cachedBlockIndex) {
            node = currentCachedNode
            startBlock = cachedBlockIndex
        } else {
            node = head
            startBlock = 0
        }

        for (block in startBlock until targetBlockIndex) {
            node = node?.next
        }

        val resultNode = node ?: throw IllegalStateException("Узел структуры данных равен null при валидном индексе")

        // Обновляем кэш
        cachedNode = resultNode
        cachedBlockIndex = targetBlockIndex

        return resultNode
    }

    @Suppress("UNCHECKED_CAST")
    public fun get(index: Int): T {
        return blockAt(index).values[index % blockCapacity] as T
    }

    // Метод inplace-модификации по индексу
    public fun set(index: Int, data: T) {
        blockAt(index).values[index % blockCapacity] = data
    }

    // Метод быстрого обмена элементов в памяти за O(1) переходов по кэшу
    public fun swap(index1: Int, index2: Int) {
        if (index1 == index2) return
        val node1 = blockAt(index1)
        val offset1 = index1 % blockCapacity
        val temp = node1.values[offset1]

        val node2 = blockAt(index2)
        val offset2 = index2 % blockCapacity

        node1.values[offset1] = node2.values[offset2]
        node2.values[offset2] = temp
    }

    public fun insert(index: Int, data: T) {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        if (index == size) {
            add(data)
            return
        }
        var node = blockAt(index)
        var offset = index % blockCapacity
        var carry: Any? = data
        
        while (node.used == blockCapacity) {
            val displaced = node.values[blockCapacity - 1]
            System.arraycopy(node.values, offset, node.values, offset + 1, blockCapacity - offset - 1)
            node.values[offset] = carry
            carry = displaced
            
            var nextNode = node.next
            if (nextNode == null) {
                nextNode = Node(blockCapacity)
                node.next = nextNode
                tail = nextNode
                nodeCount++
            }
            node = nextNode
            offset = 0
        }
        System.arraycopy(node.values, offset, node.values, offset + 1, node.used - offset)
        node.values[offset] = carry
        node.used++
        size++
        invalidateCache()
    }

    public fun remove(index: Int) {
        var node = blockAt(index)
        var offset = index % blockCapacity
        var previous: Node<T>? = null
        
        if (node != head) {
            previous = head
            while (previous != null && previous.next != node) {
                previous = previous.next
            }
        }
        
        while (true) {
            System.arraycopy(node.values, offset + 1, node.values, offset, node.used - offset - 1)
            val nextNode = node.next
            if (nextNode != null) {
                node.values[node.used - 1] = nextNode.values[0]
                previous = node
                node = nextNode
                offset = 0
            } else {
                node.values[--node.used] = null
                if (node.used == 0) {
                    if (previous == null) {
                        head = null
                        tail = null
                    } else {
                        previous.next = null
                        tail = previous
                    }
                    nodeCount--
                }
                break
            }
        }
        size--
        invalidateCache()
    }

    public fun add(data: T, traversal: TraverseStrategyInterface<T>) {
        insert(size, data, traversal)
    }

    public fun get(index: Int, traversal: TraverseStrategyInterface<T>): T {
        return get(traversal.elementIndex(index, size))
    }

    public fun insert(index: Int, data: T, traversal: TraverseStrategyInterface<T>) {
        insert(traversal.insertionIndex(index, size), data)
    }

    public fun remove(index: Int, traversal: TraverseStrategyInterface<T>) {
        remove(traversal.elementIndex(index, size))
    }

    public fun forEach(callback: ForEachCallbackInterface<T>) {
        var node = head
        while (node != null) {
            for (i in 0 until node.used) {
                @Suppress("UNCHECKED_CAST")
                callback.toDo(node.values[i] as T)
            }
            node = node.next
        }
    }

    // Использование идиоматичного функционального типа (T) -> Boolean вместо Predicate
    public fun firstThat(predicate: (T) -> Boolean): T? {
        var node = head
        while (node != null) {
            for (i in 0 until node.used) {
                @Suppress("UNCHECKED_CAST")
                val value = node.values[i] as T
                if (predicate(value)) return value
            }
            node = node.next
        }
        return null
    }

    public fun firstIndexThat(predicate: (T) -> Boolean, traversal: TraverseStrategyInterface<T>): Int {
        for (i in 0 until size) {
            if (predicate(get(traversal.elementIndex(i, size)))) return i
        }
        return -1
    }

    public fun firstThat(predicate: (T) -> Boolean, traversal: TraverseStrategyInterface<T>): T? {
        val index = firstIndexThat(predicate, traversal)
        return if (index < 0) null else get(index, traversal)
    }

    public fun toArrayList(): ArrayList<T> {
        val result = ArrayList<T>(size)
        forEach(result::add)
        return result
    }

    public fun getBlocks(): List<List<T>> {
        val blocks = ArrayList<List<T>>(nodeCount)
        var node = head
        while (node != null) {
            val values = ArrayList<T>(node.used)
            for (i in 0 until node.used) {
                @Suppress("UNCHECKED_CAST")
                values.add(node.values[i] as T)
            }
            blocks.add(Collections.unmodifiableList(values))
            node = node.next
        }
        return Collections.unmodifiableList(blocks)
    }

    public fun sort(strategy: SortStrategyInterface, type: UserTypeInterface<T>) {
        sort(strategy, type.typeComparator)
    }

    public fun sort(strategy: SortStrategyInterface, comparator: Comparator<in T>) {
        if (size <= 1) return
        strategy.sort(this, comparator)
        invalidateCache()
    }
}
