package gui

import core.CustomList
import inface.TraverseStrategyInterface

class ListIteratorController[T](
    private val customList: CustomList[T],
    private val selection: SelectionPanel
) {
  
  // Храним ЛОГИЧЕСКИЙ индекс (позицию в текущем порядке обхода)
  // -1 означает, что итератор не установлен
  private var currentLogicalIndex: Int = -1

  def getCurrentLogicalIndex: Int = currentLogicalIndex

  // Возвращает физический индекс для отрисовки, основываясь на текущей стратегии
  def getCurrentPhysicalIndex: Int = {
    if (currentLogicalIndex == -1 || customList.getSize() == 0) {
      -1
    } else {
      val traversal = selection.traversal[T]()
      // Ищем, какой физический индекс соответствует нашему логическому
      var foundPhysical = -1
      var i = 0
      val size = customList.getSize()
      while (i < size && foundPhysical == -1) {
        if (traversal.logicalIndex(i, size) == currentLogicalIndex) {
          foundPhysical = i
        }
        i += 1
      }
      foundPhysical
    }
  }

  def toBegin(): Unit = {
    if (customList.getSize() > 0) currentLogicalIndex = 0
    else currentLogicalIndex = -1
  }

  def toEnd(): Unit = {
    val size = customList.getSize()
    if (size > 0) currentLogicalIndex = size - 1
    else currentLogicalIndex = -1
  }

  def next(): Unit = {
    val size = customList.getSize()
    if (size > 0 && currentLogicalIndex < size - 1) {
      currentLogicalIndex += 1
    }
  }

  def prev(): Unit = {
    if (customList.getSize() > 0 && currentLogicalIndex > 0) {
      currentLogicalIndex -= 1
    }
  }

  def reset(): Unit = {
    currentLogicalIndex = -1
  }
  
  def validate(): Unit = {
    val size = customList.getSize()
    if (size == 0) {
      currentLogicalIndex = -1
    } else if (currentLogicalIndex >= size) {
      currentLogicalIndex = size - 1
    }
  }
}

object ListIteratorController {
  sealed trait Op
  case object TO_BEGIN extends Op
  case object TO_END extends Op
  case object NEXT extends Op
  case object PREV extends Op
}
