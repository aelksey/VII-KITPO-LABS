package traverse;

import core.CustomList;
import inface.ForEachCallbackInterface;
import inface.TraverseStrategyInterface;

// Стратегия классического линейного обхода списка
public class LinearTraverseStrategy<T> implements TraverseStrategyInterface<T> {
    @Override
    public void traverse(CustomList<T> list, ForEachCallbackInterface<T> callback) {
        list.forEach(callback);
    }
}
