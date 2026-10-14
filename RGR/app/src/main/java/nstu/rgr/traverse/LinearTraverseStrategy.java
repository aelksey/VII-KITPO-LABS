package nstu.rgr.traverse;

import nstu.rgr.core.CustomList;
import nstu.rgr.inface.ForEachCallbackInterface;
import nstu.rgr.inface.TraverseStrategyInterface;

// Стратегия классического линейного обхода списка
public class LinearTraverseStrategy<T> implements TraverseStrategyInterface<T> {
    @Override
    public void traverse(CustomList<T> list, ForEachCallbackInterface<T> callback) {
        list.forEach(callback);
    }
}
