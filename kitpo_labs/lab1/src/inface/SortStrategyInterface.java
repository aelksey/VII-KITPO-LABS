package inface;

import java.util.ArrayList;

// Интерфейс стратегии сортировки
public interface SortStrategyInterface {
    String strategyName(); // Имя алгоритма для выпадающего списка в GUI
    
    // Метод, который выполняет саму сортировку
    <T> void sort(ArrayList<T> list, UserTypeInterface<T> userType);
}
