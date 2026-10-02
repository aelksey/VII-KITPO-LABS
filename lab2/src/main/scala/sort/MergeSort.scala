package sort

import java.util.ArrayList
import inface.SortStrategyInterface
import java.util.Comparator

// Реализация интерфейса SortStrategy (MergeSort)
class MergeSortStrategy extends SortStrategyInterface {
  
  override def strategyName(): String = {
    "Сортировка слиянием"
  }

  override def sort[T](list: ArrayList[T], userType: Comparator[_ >: T]): Unit = {
    if ((list == null) || (list.size() <= 1)) {
      // Пустой return для завершения метода
    } else {
      mergeSort(list, 0, list.size() - 1, userType)
    }
  }

  private def mergeSort[T](list: ArrayList[T], l: Int, r: Int, userType: Comparator[_ >: T]): Unit = {
    if (l < r) {
      val m = l + (r - l) / 2
      mergeSort(list, l, m, userType)
      mergeSort(list, m + 1, r, userType)
      merge(list, l, m, r, userType)
    }
  }

  private def merge[T](list: ArrayList[T], l: Int, m: Int, r: Int, userType: Comparator[_ >: T]): Unit = {
    val left = new ArrayList[T](list.subList(l, m + 1))
    val right = new ArrayList[T](list.subList(m + 1, r + 1))

    var i = 0
    var j = 0
    var k = l

    while ((i < left.size()) && (j < right.size())) {
      if (userType.compare(left.get(i), right.get(j)) <= 0) {
        list.set(k, left.get(i))
        i = i + 1
      } else {
        list.set(k, right.get(j))
        j = j + 1
      }
      k = k + 1
    }

    while (i < left.size()) {
      list.set(k, left.get(i))
      i = i + 1
      k = k + 1
    }

    while (j < right.size()) {
      list.set(k, right.get(j))
      j = j + 1
      k = k + 1
    }
  }
}
