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

    // Кэш для оптимизации blockAt при частых последовательных запросах (сортировка)
    private transient Node<T> cachedNode;
    private transient int cachedBlockIndex = -1;

    public CustomList() { this(DEFAULT_BLOCK_CAPACITY); }
    public CustomList(int blockCapacity) {
        if (blockCapacity < 1) throw new IllegalArgumentException("Размер массива должен быть положительным");
        this.blockCapacity = blockCapacity;
    }

    public int getSize() { return size; }
    public int getNodeCount() { return nodeCount; }
    public int getBlockCapacity() { return blockCapacity; }

    private void invalidateCache() {
        cachedNode = null;
        cachedBlockIndex = -1;
    }

    public void setBlockCapacity(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Размер массива должен быть положительным");
        if (capacity == blockCapacity) return;
        var items = toArrayList();
        clear();
        blockCapacity = capacity;
        items.forEach(this::add);
    }

    public void clear() { 
        head = tail = null; 
        size = nodeCount = 0; 
        invalidateCache();
    }

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
        invalidateCache();
    }

    // Оптимизированный поиск блока с кэшированием позиции
    private Node<T> blockAt(int index) {
        Objects.checkIndex(index, size);
        int targetBlockIndex = index / blockCapacity;

        // Если запрашивается тот же блок, что и в прошлый раз
        if (cachedNode != null && cachedBlockIndex == targetBlockIndex) {
            return cachedNode;
        }

        Node<T> node;
        int startBlock;

        // Определяем, откуда эффективнее начать: от head или от кэша
        if (cachedNode != null && targetBlockIndex >= cachedBlockIndex) {
            node = cachedNode;
            startBlock = cachedBlockIndex;
        } else {
            node = head;
            startBlock = 0;
        }

        for (int block = startBlock; block < targetBlockIndex; block++) {
            node = node.next;
        }

        // Обновляем кэш
        cachedNode = node;
        cachedBlockIndex = targetBlockIndex;

        return node;
    }

    public T get(int index) { return blockAt(index).values[index % blockCapacity]; }

    // Новый метод inplace-модификации по индексу
    public void set(int index, T data) {
        blockAt(index).values[index % blockCapacity] = data;
    }

    // Новый метод быстрого обмена элементов в памяти за O(1) переходов по кэшу
    public void swap(int index1, int index2) {
        if (index1 == index2) return;
        Node<T> node1 = blockAt(index1);
        int offset1 = index1 % blockCapacity;
        T temp = node1.values[offset1];

        Node<T> node2 = blockAt(index2);
        int offset2 = index2 % blockCapacity;

        node1.values[offset1] = node2.values[offset2];
        node2.values[offset2] = temp;
    }

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
        invalidateCache();
    }

    public void remove(int index) {
        Node<T> node = blockAt(index);
        int offset = index % blockCapacity;
        Node<T> previous = null;
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
        invalidateCache();
    }

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

    // Избавились от ArrayList внутри поиска индекса для экономии ресурсов
    public int firstIndexThat(Predicate<? super T> predicate, TraverseStrategyInterface<T> traversal) {
        Objects.requireNonNull(predicate); Objects.requireNonNull(traversal);
        for (int i = 0; i < size; i++) {
            if (predicate.test(get(traversal.elementIndex(i, size)))) return i;
        }
        return -1;
    }

    public T firstThat(Predicate<? super T> predicate, TraverseStrategyInterface<T> traversal) {
        int index = firstIndexThat(predicate, traversal);
        return index < 0 ? null : get(index, traversal);
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> result = new ArrayList<>(size);
        forEach(result::add);
        return result;
    }

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
    
    // Теперь никакой сборки/разборки через ArrayList! Сортируем inplace.
    public void sort(SortStrategyInterface strategy, Comparator<? super T> comparator) {
        Objects.requireNonNull(strategy); Objects.requireNonNull(comparator);
        if (size <= 1) return;
        strategy.sort(this, comparator);
        invalidateCache(); // инвалидируем кэш после неконтролируемых перестановок
    }
}
