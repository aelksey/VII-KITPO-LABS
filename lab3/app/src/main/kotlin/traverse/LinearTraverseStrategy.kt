package traverse

import core.CustomList
import inface.ForEachCallbackInterface
import inface.TraverseStrategyInterface

// Стратегия классического линейного обхода списка
class LinearTraverseStrategy<T> : TraverseStrategyInterface<T> {
    override fun traverse(list: CustomList<T>, callback: ForEachCallbackInterface<T>) {
        list.forEach(callback)
    }
}
