package sort;

import java.util.ArrayList;
import core.CustomList;
import inface.SortStrategyInterface;
import java.util.Comparator;

public class MergeSortStrategy implements SortStrategyInterface {
    @Override
    public String strategyName() { return "Сортировка слиянием"; }

    @Override
    public <T> void sort(CustomList<T> list, Comparator<? super T> userType) {
        if (list == null || list.getSize() <= 1) return;
        mergeSort(list, 0, list.getSize() - 1, userType);
    }

    private <T> void mergeSort(CustomList<T> list, int l, int r, Comparator<? super T> userType) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(list, l, m, userType);
            mergeSort(list, m + 1, r, userType);
            merge(list, l, m, r, userType);
        }
    }

    private <T> void merge(CustomList<T> list, int l, int m, int r, Comparator<? super T> userType) {
        // Выделение памяти под части остается локальным
        ArrayList<T> left = new ArrayList<>(m - l + 1);
        ArrayList<T> right = new ArrayList<>(r - m);

        for (int i = l; i <= m; i++) left.add(list.get(i));
        for (int j = m + 1; j <= r; j++) right.add(list.get(j));

        int i = 0, j = 0, k = l;
        while (i < left.size() && j < right.size()) {
            if (userType.compare(left.get(i), right.get(j)) <= 0) {
                list.set(k++, left.get(i++));
            } else {
                list.set(k++, right.get(j++));
            }
        }
        while (i < left.size()) list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }
}
