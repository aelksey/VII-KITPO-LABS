package inface

import core.CustomList
import java.util.Objects

// Интерфейс для стратегий обхода списка
trait TraverseStrategyInterface[T] {
  
  // Позиция объекта в прямой последовательности
  def elementIndex(logicalIndex: Int, size: Int): Int =
    Objects.checkIndex(logicalIndex, size)

  // Позиция вставки в прямой последовательности
  def insertionIndex(logicalIndex: Int, size: Int): Int = {
    if (logicalIndex < 0) throw new IndexOutOfBoundsException(logicalIndex)
    if (logicalIndex > size) throw new IndexOutOfBoundsException(logicalIndex)
    logicalIndex
  }

  // Обратное отображение для подписей на схеме
  def logicalIndex(physicalIndex: Int, size: Int): Int =
    Objects.checkIndex(physicalIndex, size)
    
  // Выполняет обход списка и применяет callback к элементам
  def traverse(list: CustomList[T], callback: ForEachCallbackInterface[T]): Unit
}