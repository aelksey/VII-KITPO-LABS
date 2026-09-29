package sort;

import java.util.ArrayList;

import inface.SortStrategyInterface;
import java.util.Comparator;

// Реализация интерфейса SortStrategy (MergeSort)
public class MergeSortStrategy implements SortStrategyInterface {
    @Override
    public String strategyName() { return "Сортировка слиянием"; }

    @Override
    public <T> void sort(ArrayList<T> list, Comparator<? super T> userType) {
        if (list == null || list.size() <= 1) return;
        mergeSort(list, 0, list.size() - 1, userType);
    }

    private <T> void mergeSort(ArrayList<T> list, int l, int r, Comparator<? super T> userType) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(list, l, m, userType);
            mergeSort(list, m + 1, r, userType);
            merge(list, l, m, r, userType);
        }
    }

    private <T> void merge(ArrayList<T> list, int l, int m, int r, Comparator<? super T> userType) {
        ArrayList<T> left = new ArrayList<>(list.subList(l, m + 1));
        ArrayList<T> right = new ArrayList<>(list.subList(m + 1, r + 1));

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

