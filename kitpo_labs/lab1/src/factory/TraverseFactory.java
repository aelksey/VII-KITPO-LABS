package factory;

import inface.TraverseStrategyInterface;
import traverse.LinearTraverseStrategy;
import traverse.ReverseTraverseStrategy;

/**
 * Фабрика для получения стратегий обхода.
 */
public class TraverseFactory {

    @SuppressWarnings("rawtypes")
    public static TraverseStrategyInterface getStrategyByName(String name) {
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
