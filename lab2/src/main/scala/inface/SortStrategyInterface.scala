package inface

import core.CustomList
import java.util.Comparator

trait SortStrategyInterface {
  def strategyName(): String
  
  // Принимаем CustomList вместо ArrayList для реализации inplace-сортировки
  def sort[T](list: CustomList[T], comparator: Comparator[_ >: T]): Unit
  
  def sort[T](list: CustomList[T], `type`: UserTypeInterface[T]): Unit =
    sort(list, `type`.getTypeComparator())
}
