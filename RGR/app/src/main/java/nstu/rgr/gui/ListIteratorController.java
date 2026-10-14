package nstu.rgr.gui;

import nstu.rgr.core.CustomList;
import nstu.rgr.inface.TraverseStrategyInterface;

public class ListIteratorController<T> {

    public enum Op { TO_BEGIN, TO_END, NEXT, PREV }

    private final CustomList<T> customList;

    // Храним ЛОГИЧЕСКИЙ индекс (позицию в текущем порядке обхода)
    // -1 означает, что итератор не установлен
    private int currentLogicalIndex = -1;

    public ListIteratorController(CustomList<T> customList, Object ignored) {
        this.customList = customList;
    }

    public int getCurrentLogicalIndex() {
        return currentLogicalIndex;
    }

    // Возвращает физический индекс для отрисовки, основываясь на переданной стратегии
    public int getCurrentPhysicalIndex(TraverseStrategyInterface<T> traversal) {
        if (currentLogicalIndex == -1 || customList.getSize() == 0 || traversal == null) {
            return -1;
        } else {
            int foundPhysical = -1;
            int size = customList.getSize();

            for (int i = 0; i < size; i++) {
                if (traversal.logicalIndex(i, size) == currentLogicalIndex) {
                    foundPhysical = i;
                    break;
                }
            }
            return foundPhysical;
        }
    }

    public void toBegin() {
        if (customList.getSize() > 0) {
            currentLogicalIndex = 0;
        } else {
            currentLogicalIndex = -1;
        }
    }

    public void toEnd() {
        int size = customList.getSize();
        if (size > 0) {
            currentLogicalIndex = size - 1;
        } else {
            currentLogicalIndex = -1;
        }
    }

    public void next() {
        int size = customList.getSize();
        if (size > 0 && currentLogicalIndex < size - 1) {
            currentLogicalIndex++;
        }
    }

    public void prev() {
        if (customList.getSize() > 0 && currentLogicalIndex > 0) {
            currentLogicalIndex--;
        }
    }

    public void reset() {
        currentLogicalIndex = -1;
    }

    public void validate() {
        int size = customList.getSize();
        if (size == 0) {
            currentLogicalIndex = -1;
        } else if (currentLogicalIndex >= size) {
            currentLogicalIndex = size - 1;
        }
    }
}
