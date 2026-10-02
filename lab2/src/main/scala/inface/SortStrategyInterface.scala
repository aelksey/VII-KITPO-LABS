package inface

import java.util.{ArrayList, Comparator}

trait SortStrategyInterface {
  def strategyName(): String
  
  // Базовый метод сортировки, принимающий стандартный Java Comparator
  def sort[T](list: ArrayList[T], comparator: Comparator[_ >: T]): Unit
  
  // Метод с реализацией по умолчанию, использующий компаратор из UserTypeInterface
  def sort[T](list: ArrayList[T], `type`: UserTypeInterface[T]): Unit =
    sort(list, `type`.getTypeComparator())
}