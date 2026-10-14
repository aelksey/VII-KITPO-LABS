package factory;

import inface.TraverseStrategyInterface;
import java.util.List;
import traverse.LinearTraverseStrategy;
import traverse.ReverseTraverseStrategy;

// Фабрика для получения стратегий обхода на Java
public class TraverseFactory {

    public static List<String> getStrategyNameList() {
        return List.of("Линейный обход", "Обратный обход");
    }

    @SuppressWarnings("unchecked")
    public static <T> TraverseStrategyInterface<T> getStrategyByName(String name) {
        if (name == null) {
            return null;
        }
        
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
