package sort

import core.CustomList
import inface.SortStrategyInterface
import java.util.ArrayList
import java.util.Comparator

class MergeSortStrategy extends SortStrategyInterface {
  
  override def strategyName(): String = "Сортировка слиянием"

  override def sort[T](list: CustomList[T], userType: Comparator[_ >: T]): Unit = {
    if ((list == null) || (list.getSize() <= 1)) {
      // Базовый случай
    } else {
      mergeSort(list, 0, list.getSize() - 1, userType)
    }
  }

  private def mergeSort[T](list: CustomList[T], l: Int, r: Int, userType: Comparator[_ >: T]): Unit = {
    if (l < r) {
      val m = l + (r - l) / 2
      mergeSort(list, l, m, userType)
      mergeSort(list, m + 1, r, userType)
      merge(list, l, m, r, userType)
    }
  }

  private def merge[T](list: CustomList[T], l: Int, m: Int, r: Int, userType: Comparator[_ >: T]): Unit = {
  val left = new ArrayList[T](m - l + 1)
  val right = new ArrayList[T](r - m)

  // Использовался оператор '<=', заменено на 'to' для генерации диапазона индексов
  for (i <- l to m) left.add(list.get(i))
  for (j <- (m + 1) to r) right.add(list.get(j))

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
