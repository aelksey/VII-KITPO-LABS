package inface;

import core.CustomList;
import java.util.Comparator;

public interface SortStrategyInterface {
    String strategyName();
    <T> void sort(CustomList<T> list, Comparator<? super T> comparator);
    default <T> void sort(CustomList<T> list, UserTypeInterface<T> type) {
        sort(list, type.getTypeComparator());
    }
}
