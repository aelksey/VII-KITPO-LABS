package sort;

import core.CustomList;
import inface.SortStrategyInterface;
import java.util.ArrayList;
import java.util.Comparator;

public class QuickSortStrategy implements SortStrategyInterface {
    public String strategyName() { return "Быстрая сортировка"; }

    public <T> void sort(CustomList<T> list, Comparator<? super T> comparator) {
        if (list == null || list.getSize() < 2) return;
        quickSort(list, 0, list.getSize() - 1, 2 * (31 - Integer.numberOfLeadingZeros(list.getSize())), comparator);
    }

    private <T> void quickSort(CustomList<T> list, int low, int high, int depth, Comparator<? super T> c) {
        if (low >= high) return;
        if (depth == 0) {
            // При ограничении глубины создаем временный CustomList для внутренней MergeSort
            CustomList<T> part = new CustomList<>(list.getBlockCapacity());
            for (int i = low; i <= high; i++) part.add(list.get(i));
            
            new MergeSortStrategy().sort(part, c);
            
            for (int i = 0; i < part.getSize(); i++) list.set(low + i, part.get(i));
            return;
        }
        
        T pivot = list.get(low + (high - low) / 2);
        int i = low, j = high;
        while (i <= j) {
            while (c.compare(list.get(i), pivot) < 0) i++;
            while (c.compare(list.get(j), pivot) > 0) j--;
            if (i <= j) { 
                list.swap(i++, j--); // Наш атомарный swap
            }
        }
        quickSort(list, low, j, depth - 1, c);
        quickSort(list, i, high, depth - 1, c);
    }
}
