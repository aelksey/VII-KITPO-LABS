package traverse;

import core.CustomList;
import inface.ForEachCallbackInterface;
import inface.TraverseStrategyInterface;

// Стратегия обхода списка в обратном порядке
public class ReverseTraverseStrategy<T> implements TraverseStrategyInterface<T> {
    @Override public int elementIndex(int logicalIndex, int size) {
        return size - 1 - TraverseStrategyInterface.super.elementIndex(logicalIndex, size);
    }
    @Override public int insertionIndex(int logicalIndex, int size) {
        return size - TraverseStrategyInterface.super.insertionIndex(logicalIndex, size);
    }
    @Override public int logicalIndex(int physicalIndex, int size) {
        return elementIndex(physicalIndex, size);
    }
    @Override
    public void traverse(CustomList<T> list, ForEachCallbackInterface<T> callback) {
        var values = list.toArrayList();
        int size = values.size();
        for (int i = size - 1; i >= 0; i--) {
            callback.toDo(values.get(i));
        }
    }
}
