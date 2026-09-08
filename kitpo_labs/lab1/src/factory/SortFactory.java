package factory;

import java.util.ArrayList;
import java.util.List;


import inface.SortStrategyInterface;
import sort.*;

// Фабрика стратегий сортировок
public class SortFactory {
    private static final List<SortStrategyInterface> strategies = new ArrayList<>();

    static {
        // Регистрация алгоритмов сортировки
        strategies.add(new QuickSortStrategy());
        strategies.add(new MergeSortStrategy());
    }

    public static List<String> getStrategyNameList() {
        List<String> names = new ArrayList<>();
        for (SortStrategyInterface s : strategies) {
            names.add(s.strategyName());
        }
        return names;
    }

    public static SortStrategyInterface getStrategyByName(String name) {
        for (SortStrategyInterface s : strategies) {
            if (s.strategyName().equals(name)) {
                return s;
            }
        }
        // По умолчанию возвращаем первый алгоритм, если что-то пошло не так
        return strategies.get(0);
    }
}

