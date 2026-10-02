package sort

import inface.SortStrategyInterface
import java.util.ArrayList
import java.util.Comparator
import java.util.Collections

// QuickSort с ограничением глубины
class QuickSortStrategy extends SortStrategyInterface {
  
  override def strategyName(): String = {
    "Быстрая сортировка"
  }

  override def sort[T](list: ArrayList[T], comparator: Comparator[_ >: T]): Unit = {
    if ((list == null) || (list.size() < 2)) {
      // Пустой return для завершения метода
    } else {
      val maxDepth = 2 * (31 - java.lang.Integer.numberOfLeadingZeros(list.size()))
      quickSort(list, 0, list.size() - 1, maxDepth, comparator)
    }
  }

  private def quickSort[T](list: ArrayList[T], low: Int, high: Int, depth: Int, c: Comparator[_ >: T]): Unit = {
    if (low >= high) {
      // Базовый случай рекурсии
    } else {
      if (depth == 0) {
        val part = new ArrayList[T](list.subList(low, high + 1))
        new MergeSortStrategy().sort(part, c)
        for (i <- 0 until part.size()) {
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
            Collections.swap(list, i, j)
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
