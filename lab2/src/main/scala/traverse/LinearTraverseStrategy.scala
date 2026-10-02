package traverse

import core.CustomList
import inface.ForEachCallbackInterface
import inface.TraverseStrategyInterface

// Стратегия классического линейного обхода списка
class LinearTraverseStrategy[T] extends TraverseStrategyInterface[T] {
  
  override def traverse(list: CustomList[T], callback: ForEachCallbackInterface[T]): Unit = {
    // Явно оборачиваем вызов интерфейса в лямбду, которую ждет метод списка
    list.forEach { item => {
      callback.toDo(item)
    }}
  }
}
