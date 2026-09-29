package factory;

import inface.TraverseStrategyInterface;
import traverse.LinearTraverseStrategy;
import traverse.ReverseTraverseStrategy;

// Фабрика для получения стратегий обхода.
public class TraverseFactory {

    public static java.util.List<String> getStrategyNameList() {
        return java.util.List.of("Линейный обход", "Обратный обход");
    }

    public static <T> TraverseStrategyInterface<T> getStrategyByName(String name) {
        if (name == null) return null;
        
        switch (name.trim()) {
            case "Линейный обход":
                return new LinearTraverseStrategy<>();
            case "Обратный обход":
                return new ReverseTraverseStrategy<>();
            default:
                return null;
        }
    }
}
