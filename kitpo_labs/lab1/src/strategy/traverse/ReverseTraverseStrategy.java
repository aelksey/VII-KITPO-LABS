package traverse;

import core.CustomList;
import inface.ForEachCallbackInterface;
import inface.TraverseStrategyInterface;

/**
 * Стратегия обхода списка в обратном порядке (с конца в начало).
 */
public class ReverseTraverseStrategy<T> implements TraverseStrategyInterface<T> {
    @Override
    public void traverse(CustomList<T> list, ForEachCallbackInterface<T> callback) {
        int size = list.getSize();
        for (int i = size - 1; i >= 0; i--) {
            callback.toDo(list.get(i));
        }
    }
}

