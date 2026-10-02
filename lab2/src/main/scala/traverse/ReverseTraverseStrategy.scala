package traverse

import core.CustomList
import inface.ForEachCallbackInterface
import inface.TraverseStrategyInterface

// Стратегия обхода списка в обратном порядке
class ReverseTraverseStrategy[T] extends TraverseStrategyInterface[T] {
  
  override def elementIndex(logicalIndex: Int, size: Int): Int = {
    size - 1 - super[TraverseStrategyInterface].elementIndex(logicalIndex, size)
  }

  override def insertionIndex(logicalIndex: Int, size: Int): Int = {
    size - super[TraverseStrategyInterface].insertionIndex(logicalIndex, size)
  }

  override def logicalIndex(physicalIndex: Int, size: Int): Int = {
    elementIndex(physicalIndex, size)
  }

  override def traverse(list: CustomList[T], callback: ForEachCallbackInterface[T]): Unit = {
    val values = list.toArrayList()
    val size = values.size()
    var i = size - 1
    
    while (i >= 0) {
      callback.toDo(values.get(i))
      i = i - 1
    }
  }
}
