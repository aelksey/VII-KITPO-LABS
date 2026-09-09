package sort;

import java.util.ArrayList;

import inface.SortStrategyInterface;
import inface.UserTypeInterface;

// Реализация интерфейса SortStrategy (QuickSort)
public class QuickSortStrategy implements SortStrategyInterface {
    @Override
    public String strategyName() { return "Быстрая сортировка (QuickSort)"; }

    @Override
    public <T> void sort(ArrayList<T> list, UserTypeInterface<T> userType) {
        if (list == null || list.size() <= 1) return;
        quickSort(list, 0, list.size() - 1, userType);
    }

    private <T> void quickSort(ArrayList<T> list, int low, int high, UserTypeInterface<T> userType) {
        if (low < high) {
            int pi = partition(list, low, high, userType);
            quickSort(list, low, pi - 1, userType);
            quickSort(list, pi + 1, high, userType);
        }
    }

    private <T> int partition(ArrayList<T> list, int low, int high, UserTypeInterface<T> userType) {
        T pivot = list.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (userType.compare(list.get(j), pivot) <= 0) {
                i++;
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        T temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        return i + 1;
    }
}
