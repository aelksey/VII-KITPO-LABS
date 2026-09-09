package core;

import java.util.ArrayList;
import java.util.Arrays;

import inface.ForEachCallbackInterface;
import inface.SortStrategyInterface;
import inface.UserTypeInterface;

/**
 * Структура данных: Связный список, где каждый элемент содержит
 * динамически расширяемый массив ссылок (next) на другие элементы.
 * @param <T> Пользовательский тип данных.
 */
public class CustomList<T> {

    private static class Node<E> {
        E data;
        Node<E>[] next;

        @SuppressWarnings("unchecked")
        Node(E data, int initialLevels) {
            this.data = data;
            this.next = (Node<E>[]) new Node[initialLevels];
        }

        // Расширение массива ссылок при нехватке уровней.
        // Вызывается только когда действительно нужен уровень выше текущего максимума.
        public void ensureLevels(int requiredLevels) {
            if (requiredLevels > next.length) {
                int newLength = Math.max(next.length * 2, requiredLevels);
                next = Arrays.copyOf(next, newLength);
            }
        }

        // Эффективная длина массива ссылок (без null-хвоста).
        // Используется для визуализации, чтобы не показывать пустые уровни.
        public int getUsedLevels() {
            int used = 0;
            for (int i = 0; i < next.length; i++) {
                if (next[i] != null) used = i + 1;
            }
            return used;
        }
    }

    private Node<T> head = null;
    private int size = 0;

    private static final int INITIAL_LEVELS = 1;

    public int getSize() {
        return size;
    }

    // Очистка списка
    public void clear() {
        head = null;
        size = 0;
    }

    // 1. Добавление в конец списка
    public void add(T data) {
        Node<T> newNode = new Node<>(data, INITIAL_LEVELS);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            // Убираем лишние ensureLevels(1) — массив уже размера 1 при создании
            while (current.next[0] != null) {
                current = current.next[0];
            }
            current.next[0] = newNode;
        }
        size++;
    }

    // 2. Получение элемента по логическому индексу
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + size);
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next[0];
        }
        return current.data;
    }

    // 3. Вставка элемента по логическому индексу
    public void insert(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Size: " + size);
        }

        if (index == 0) {
            Node<T> newNode = new Node<>(data, INITIAL_LEVELS);
            newNode.next[0] = head;
            head = newNode;
            size++;
            return;
        }

        Node<T> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next[0];
        }

        Node<T> newNode = new Node<>(data, INITIAL_LEVELS);
        newNode.next[0] = current.next[0];
        current.next[0] = newNode;
        size++;
    }

    // 4. Удаление элемента по логическому индексу
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс: " + index + ", Размер: " + size);
        }

        if (index == 0) {
            head = head.next[0];
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next[0];
            }
            current.next[0] = current.next[0].next[0];
        }
        size--;
    }

    /**
     * Установка ссылки на произвольный уровень (индекс массива next).
     * Фикс инкапсуляции: принимаем индекс целевого элемента, а не Node извне.
     * Метод сам ищет нужный узел и расширяет массив при необходимости.
     *
     * @param elementIndex индекс элемента, у которого задаём ссылку
     * @param level        уровень (индекс в массиве next), начиная с 0
     * @param targetIndex  индекс элемента, на который ссылается,
     *                     или -1 для null (обнуление ссылки)
     */
    public void setLinkAtLevel(int elementIndex, int level, int targetIndex) {
        if (elementIndex < 0 || elementIndex >= size) {
            throw new IndexOutOfBoundsException(
                "Индекс элемента: " + elementIndex + ", Размер: " + size);
        }

        // Находим исходный узел
        Node<T> node = head;
        for (int i = 0; i < elementIndex; i++) {
            node = node.next[0];
        }

        // Находим целевой узел (или null)
        Node<T> target = null;
        if (targetIndex >= 0) {
            if (targetIndex >= size) {
                throw new IndexOutOfBoundsException(
                    "Индекс цели: " + targetIndex + ", Размер: " + size);
            }
            target = head;
            for (int i = 0; i < targetIndex; i++) {
                target = target.next[0];
            }
        }

        // Расширяем массив ссылок при необходимости и ставим ссылку
        node.ensureLevels(level + 1);
        node.next[level] = target;
    }

    // 5. Итератор forEach
    public void forEach(ForEachCallbackInterface<T> callback) {
        Node<T> current = head;
        while (current != null) {
            callback.toDo(current.data);
            current = current.next[0];
        }
    }


    // 6. Сортировка с помощью паттерна "Стратегия"
    // Внимание: после сортировки все ссылки на уровнях выше 0 сбрасываются,
    // т.к. список пересоздаётся. Если нужны многоуровневые связи —
    // перестраивайте их после вызова sort().
    public void sort(SortStrategyInterface strategy, UserTypeInterface<T> UserTypeInterface) {
        if (size <= 1) return;

        ArrayList<T> items = new ArrayList<>();
        forEach(items::add);

        strategy.sort(items, UserTypeInterface);

        head = null;
        size = 0;
        for (T item : items) {
            add(item);
        }
    }

    // 7. Визуализация структуры списка
    // Показывает каждый элемент, количество занятых уровней и куда они ведут.
    public void printStructure(UserTypeInterface<T> UserTypeInterface) {
        if (head == null) {
            System.out.println("Список пуст");
            return;
        }

        Node<T> current = head;
        int idx = 0;

        System.out.println("═══════════════════════════════════════");
        System.out.printf("CustomList (size=%d)%n", size);
        System.out.println("═══════════════════════════════════════");

        while (current != null) {
            int usedLevels = current.getUsedLevels();
            int capacity = current.next.length;

            String dataStr;
            if (UserTypeInterface != null) {
                dataStr = UserTypeInterface.toString(current.data);
            } else {
                // Если UserTypeInterface не передали, используем стандартный toString объекта
                dataStr = current.data != null ? current.data.toString() : "null";
            }

            System.out.printf("[%d] data=%s  | занято уровней: %d / ёмкость: %d%n",
                idx, dataStr, usedLevels, capacity);

            for (int l = 0; l < capacity; l++) {
                if (current.next[l] != null) {
                    String targetStr = (UserTypeInterface != null)
                        ? UserTypeInterface.toString(current.next[l].data)
                        : current.next[l].data.toString();
                    System.out.printf("    L%d -> %s%n", l, targetStr);
                }
            }

            current = current.next[0];
            idx++;
        }
        System.out.println("═══════════════════════════════════════");
    }
}
