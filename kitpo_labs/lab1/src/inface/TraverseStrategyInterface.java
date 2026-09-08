package inface;

import core.CustomList;

/**
 * Интерфейс для стратегий обхода (итерации) списка.
 */
public interface TraverseStrategyInterface<T> {
    /**
     * Выполняет обход списка и применяет callback к элементам 
     * в порядке, определяемом конкретной стратегией.
     */
    void traverse(CustomList<T> list, ForEachCallbackInterface<T> callback);
}
