package traverse

import core.CustomList
import inface.ForEachCallbackInterface
import inface.TraverseStrategyInterface
import java.util.ArrayList

// Стратегия обхода списка в обратном порядке
class ReverseTraverseStrategy<T> : TraverseStrategyInterface<T> {
    
    override fun elementIndex(logicalIndex: Int, size: Int): Int {
        return size - 1 - super<TraverseStrategyInterface>.elementIndex(logicalIndex, size)
    }

    override fun insertionIndex(logicalIndex: Int, size: Int): Int {
        return size - super<TraverseStrategyInterface>.insertionIndex(logicalIndex, size)
    }

    override fun logicalIndex(physicalIndex: Int, size: Int): Int {
        return elementIndex(physicalIndex, size)
    }

    override fun traverse(list: CustomList<T>, callback: ForEachCallbackInterface<T>) {
        val values = list.toArrayList()
        val size = values.size
        for (i in size - 1 downTo 0) {
            callback.toDo(values[i])
        }
    }
}
