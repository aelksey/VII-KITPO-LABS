package inface;

import java.util.ArrayList;
import java.util.Comparator;

public interface SortStrategyInterface {
    String strategyName();
    <T> void sort(ArrayList<T> list, Comparator<? super T> comparator);
    default <T> void sort(ArrayList<T> list, UserTypeInterface<T> type) {
        sort(list, type.getTypeComparator());
    }
}
