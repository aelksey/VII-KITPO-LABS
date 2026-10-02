package sort

import core.CustomList
import inface.SortStrategyInterface
import java.util.Comparator

class QuickSortStrategy extends SortStrategyInterface {
  
  override def strategyName(): String = "Быстрая сортировка"

  override def sort[T](list: CustomList[T], comparator: Comparator[_ >: T]): Unit = {
    if ((list == null) || (list.getSize() < 2)) {
      // Базовый случай
    } else {
      val maxDepth = 2 * (31 - java.lang.Integer.numberOfLeadingZeros(list.getSize()))
      quickSort(list, 0, list.getSize() - 1, maxDepth, comparator)
    }
  }

  private def quickSort[T](list: CustomList[T], low: Int, high: Int, depth: Int, c: Comparator[_ >: T]): Unit = {
    if (low >= high) {
      // Выход из рекурсии
    } else {
      if (depth == 0) {
        // При деградации глубины рекурсии сортируем кусок через MergeSort внутри временного контейнера
        val part = new CustomList[T](list.getBlockCapacity())
        for (i <- low to high) {
          part.add(list.get(i))
        }
        new MergeSortStrategy().sort(part, c)
        for (i <- 0 until part.getSize()) {
          list.set(low + i, part.get(i))
        }
      } else {
        val pivot = list.get(low + (high - low) / 2)
        var i = low
        var j = high

        while (i <= j) {
          while (c.compare(list.get(i), pivot) < 0) {
            i = i + 1
          }
          while (c.compare(list.get(j), pivot) > 0) {
            j = j - 1
          }
          if (i <= j) {
            list.swap(i, j) // Inplace атомарный обмен внутри блоков памяти узла
            i = i + 1
            j = j - 1
          }
        }
        quickSort(list, low, j, depth - 1, c)
        quickSort(list, i, high, depth - 1, c)
      }
    }
  }
}
