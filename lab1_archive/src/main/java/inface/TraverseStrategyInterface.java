package inface;

import core.CustomList;

// Интерфейс для стратегий обхода списка
public interface TraverseStrategyInterface<T> {
    // Позиция объекта в прямой последовательности заполненных ячеек по номеру обхода.
    default int elementIndex(int logicalIndex, int size) {
        return java.util.Objects.checkIndex(logicalIndex, size);
    }

    // Позиция вставки в прямой последовательности: все промежутки от 0 до size.
    default int insertionIndex(int logicalIndex, int size) {
        if (logicalIndex < 0 || logicalIndex > size) throw new IndexOutOfBoundsException(logicalIndex);
        return logicalIndex;
    }

    // Обратное отображение для подписей на схеме.
    default int logicalIndex(int physicalIndex, int size) {
        return java.util.Objects.checkIndex(physicalIndex, size);
    }
    // Выполняет обход списка и применяет callback к элементам
    void traverse(CustomList<T> list, ForEachCallbackInterface<T> callback);
}
