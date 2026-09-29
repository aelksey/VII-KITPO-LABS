package sort;

import inface.SortStrategyInterface;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

// QuickSort с ограничением глубины

public class QuickSortStrategy implements SortStrategyInterface {
    public String strategyName() { return "Быстрая сортировка"; }

    public <T> void sort(ArrayList<T> list, Comparator<? super T> comparator) {
        if (list == null || list.size() < 2) return;
        quickSort(list, 0, list.size() - 1, 2 * (31 - Integer.numberOfLeadingZeros(list.size())), comparator);
    }

    private <T> void quickSort(ArrayList<T> list, int low, int high, int depth, Comparator<? super T> c) {
        if (low >= high) return;
        if (depth == 0) {
            ArrayList<T> part = new ArrayList<>(list.subList(low, high + 1));
            new MergeSortStrategy().sort(part, c);
            for (int i = 0; i < part.size(); i++) list.set(low + i, part.get(i));
            return;
        }
        T pivot = list.get(low + (high - low) / 2);
        int i = low, j = high;
        while (i <= j) {
            while (c.compare(list.get(i), pivot) < 0) i++;
            while (c.compare(list.get(j), pivot) > 0) j--;
            if (i <= j) { Collections.swap(list, i++, j--); }
        }
        quickSort(list, low, j, depth - 1, c);
        quickSort(list, i, high, depth - 1, c);
    }
}
