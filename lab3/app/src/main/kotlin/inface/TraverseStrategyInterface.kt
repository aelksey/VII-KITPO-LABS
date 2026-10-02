package inface

import core.CustomList

// Интерфейс для стратегий обхода списка
interface TraverseStrategyInterface<T> {

    // Позиция объекта в прямой последовательности заполненных ячеек по номеру обхода.
    fun elementIndex(logicalIndex: Int, size: Int): Int {
        if (logicalIndex !in 0 until size) throw IndexOutOfBoundsException("Index: $logicalIndex, Size: $size")
        return logicalIndex
    }

    // Позиция вставки в прямой последовательности: все промежутки от 0 до size.
    fun insertionIndex(logicalIndex: Int, size: Int): Int {
        if (logicalIndex < 0 || logicalIndex > size) throw IndexOutOfBoundsException("Index: $logicalIndex, Size: $size")
        return logicalIndex
    }

    // Обратное отображение для подписей на схеме.
    fun logicalIndex(physicalIndex: Int, size: Int): Int {
        if (physicalIndex !in 0 until size) throw IndexOutOfBoundsException("Index: $physicalIndex, Size: $size")
        return physicalIndex
    }

    // Выполняет обход списка и применяет callback к элементам
    fun traverse(list: CustomList<T>, callback: ForEachCallbackInterface<T>)
}
