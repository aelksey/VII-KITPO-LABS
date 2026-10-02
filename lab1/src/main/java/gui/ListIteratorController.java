package gui;

import core.CustomList;
import inface.TraverseStrategyInterface;

public class ListIteratorController<T> {
    
    public enum Op { TO_BEGIN, TO_END, NEXT, PREV }

    private final CustomList<T> customList;
    private final SelectionPanel selection;
    
    // Храним ЛОГИЧЕСКИЙ индекс (позицию в текущем порядке обхода)
    // -1 означает, что итератор не установлен
    private int currentLogicalIndex = -1;

    public ListIteratorController(CustomList<T> customList, SelectionPanel selection) {
        this.customList = customList;
        this.selection = selection;
    }

    public int getCurrentLogicalIndex() {
        return currentLogicalIndex;
    }

    // Возвращает физический индекс для отрисовки, основываясь на текущей стратегии
    public int getCurrentPhysicalIndex() {
        if (currentLogicalIndex == -1 || customList.getSize() == 0) {
            return -1;
        } else {
            TraverseStrategyInterface<T> traversal = selection.traversal();
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
