package gui

import core.CustomList
import inface.TraverseStrategyInterface

class ListIteratorController<T>(
    private val customList: CustomList<T>, 
    private val selection: SelectionPanel
) {
    enum class Op { TO_BEGIN, TO_END, NEXT, PREV }

    // Храним ЛОГИЧЕСКИЙ индекс (позицию в текущем порядке обхода)
    // -1 означает, что итератор не установлен
    var currentLogicalIndex: Int = -1
        private set

    // Возвращает физический индекс для отрисовки, основываясь на текущей стратегии
    val currentPhysicalIndex: Int
        get() {
            val size = customList.size
            if (currentLogicalIndex == -1 || size == 0) {
                return -1
            } else {
                val traversal = selection.traversal<T>()
                var foundPhysical = -1
                
                for (i in 0 until size) {
                    if (traversal.logicalIndex(i, size) == currentLogicalIndex) {
                        foundPhysical = i
                        break
                    }
                }
                return foundPhysical
            }
        }

    fun toBegin() {
        currentLogicalIndex = if (customList.size > 0) 0 else -1
    }

    fun toEnd() {
        val size = customList.size
        currentLogicalIndex = if (size > 0) size - 1 else -1
    }

    fun next() {
        val size = customList.size
        if (size > 0 && currentLogicalIndex < size - 1) {
            currentLogicalIndex++
        }
    }

    fun prev() {
        if (customList.size > 0 && currentLogicalIndex > 0) {
            currentLogicalIndex--
        }
    }

    fun reset() {
        currentLogicalIndex = -1
    }

    fun validate() {
        val size = customList.size
        if (size == 0) {
            currentLogicalIndex = -1
        } else if (currentLogicalIndex >= size) {
            currentLogicalIndex = size - 1
        }
    }
}
