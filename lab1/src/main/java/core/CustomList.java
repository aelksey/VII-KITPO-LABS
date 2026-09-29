package core;

import inface.ForEachCallbackInterface;
import inface.SortStrategyInterface;
import inface.UserTypeInterface;
import inface.TraverseStrategyInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

// Список блоков
public class CustomList<T> {
    private static final class Node<E> {
        final E[] values;
        int used;
        Node<E> next;
        @SuppressWarnings("unchecked")
        Node(int capacity) { values = (E[]) new Object[capacity]; }
    }

    public static final int DEFAULT_BLOCK_CAPACITY = 3;
    private Node<T> head;
    private Node<T> tail;
    private int size;
    private int nodeCount;
    private int blockCapacity;

    public CustomList() { this(DEFAULT_BLOCK_CAPACITY); }
    public CustomList(int blockCapacity) {
        if (blockCapacity < 1) throw new IllegalArgumentException("Размер массива должен быть положительным");
        this.blockCapacity = blockCapacity;
    }

    // Количество объектов, а не узлов
    public int getSize() { return size; }
    public int getNodeCount() { return nodeCount; }
    public int getBlockCapacity() { return blockCapacity; }

    // Перегруппировка ссылок; сами объекты и их порядок не меняются
    public void setBlockCapacity(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Размер массива должен быть положительным");
        if (capacity == blockCapacity) return;
        var items = toArrayList();
        clear();
        blockCapacity = capacity;
        items.forEach(this::add);
    }

    public void clear() { head = tail = null; size = nodeCount = 0; }

    // Добавление ссылки в последнюю свободную ячейку; при необходимости создаётся узел
    public void add(T data) {
        if (tail == null || tail.used == blockCapacity) {
            Node<T> node = new Node<>(blockCapacity);
            if (tail == null) head = node;
            else tail.next = node;
            tail = node;
            nodeCount++;
        }
        tail.values[tail.used++] = data;
        size++;
    }

    private Node<T> blockAt(int index) {
        Objects.checkIndex(index, size);
        Node<T> node = head;
        for (int block = index / blockCapacity; block > 0; block--) node = node.next;
        return node;
    }

    public T get(int index) { return blockAt(index).values[index % blockCapacity]; }

    // Вставляет ссылку, сдвигая последующие ссылки через границы массивов
    public void insert(int index, T data) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
        if (index == size) { add(data); return; }
        Node<T> node = blockAt(index);
        int offset = index % blockCapacity;
        T carry = data;
        while (node.used == blockCapacity) {
            T displaced = node.values[blockCapacity - 1];
            System.arraycopy(node.values, offset, node.values, offset + 1, blockCapacity - offset - 1);
            node.values[offset] = carry;
            carry = displaced;
            if (node.next == null) {
                node.next = new Node<>(blockCapacity);
                tail = node.next;
                nodeCount++;
            }
            node = node.next;
            offset = 0;
        }
        System.arraycopy(node.values, offset, node.values, offset + 1, node.used - offset);
        node.values[offset] = carry;
        node.used++;
        size++;
    }

    // Удаляет ссылку и уплотняет последующие массивы
    public void remove(int index) {
        Node<T> node = blockAt(index);
        int offset = index % blockCapacity;
        Node<T> previous = null;
        // Предшественник нужен, если удаляется единственная ячейка последнего узла.
        if (node != head) {
            previous = head;
            while (previous.next != node) previous = previous.next;
        }
        while (true) {
            System.arraycopy(node.values, offset + 1, node.values, offset, node.used - offset - 1);
            if (node.next != null) {
                node.values[node.used - 1] = node.next.values[0];
                previous = node;
                node = node.next;
                offset = 0;
            } else {
                node.values[--node.used] = null;
                if (node.used == 0) {
                    if (previous == null) head = tail = null;
                    else { previous.next = null; tail = previous; }
                    nodeCount--;
                }
                break;
            }
        }
        size--;
    }

    // Перегрузки с выбранным обходом
    public void add(T data, TraverseStrategyInterface<T> traversal) { insert(size, data, traversal); }
    public T get(int index, TraverseStrategyInterface<T> traversal) { return get(traversal.elementIndex(index, size)); }
    public void insert(int index, T data, TraverseStrategyInterface<T> traversal) { insert(traversal.insertionIndex(index, size), data); }
    public void remove(int index, TraverseStrategyInterface<T> traversal) { remove(traversal.elementIndex(index, size)); }

    public void forEach(ForEachCallbackInterface<T> callback) {
        Objects.requireNonNull(callback);
        for (Node<T> node = head; node != null; node = node.next)
            for (int i = 0; i < node.used; i++) callback.toDo(node.values[i]);
    }

    public T firstThat(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        for (Node<T> node = head; node != null; node = node.next)
            for (int i = 0; i < node.used; i++) if (predicate.test(node.values[i])) return node.values[i];
        return null;
    }
    public T firstThat(Predicate<? super T> predicate, TraverseStrategyInterface<T> traversal) {
        int index = firstIndexThat(predicate, traversal);
        return index < 0 ? null : get(index, traversal);
    }
    public int firstIndexThat(Predicate<? super T> predicate, TraverseStrategyInterface<T> traversal) {
        Objects.requireNonNull(predicate); Objects.requireNonNull(traversal);
        var values = toArrayList();
        for (int i = 0; i < values.size(); i++)
            if (predicate.test(values.get(traversal.elementIndex(i, values.size())))) return i;
        return -1;
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> result = new ArrayList<>(size);
        forEach(result::add);
        return result;
    }

    // Снимок заполненных ячеек каждого узла. Оболочки неизменяемые, объекты те же
    public List<List<T>> getBlocks() {
        List<List<T>> blocks = new ArrayList<>(nodeCount);
        for (Node<T> node = head; node != null; node = node.next) {
            List<T> values = new ArrayList<>(node.used);
            for (int i = 0; i < node.used; i++) values.add(node.values[i]);
            blocks.add(Collections.unmodifiableList(values));
        }
        return Collections.unmodifiableList(blocks);
    }

    public void sort(SortStrategyInterface strategy, UserTypeInterface<T> type) { sort(strategy, type.getTypeComparator()); }
    // O(N log N) + O(N) сборка блоков. Переставляются ссылки, объекты не клонируются
    public void sort(SortStrategyInterface strategy, Comparator<? super T> comparator) {
        Objects.requireNonNull(strategy); Objects.requireNonNull(comparator);
        if (size <= 1) return;
        var items = toArrayList();
        strategy.sort(items, comparator);
        clear();
        items.forEach(this::add);
    }
}
