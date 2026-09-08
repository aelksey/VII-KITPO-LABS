package factory;

import java.util.ArrayList;
import java.util.List;

import inface.SerializeStrategyInterface;
import serialize.*;

// Фабрика стратегий сериализации
public class SerializeFactory {
    private static final List<SerializeStrategyInterface> strategies = new ArrayList<>();

    static {
        strategies.add(new PlainTxtSerializeStrategy());
        strategies.add(new BinarySerializeStrategy());
        strategies.add(new JsonSerializeStrategy());
    }

    public static List<String> getFormatNameList() {
        List<String> names = new ArrayList<>();
        for (SerializeStrategyInterface s : strategies) {
            names.add(s.formatName());
        }
        return names;
    }

    public static SerializeStrategyInterface getStrategyByName(String name) {
        for (SerializeStrategyInterface s : strategies) {
            if (s.formatName().equals(name)) {
                return s;
            }
        }
        return strategies.get(0); // Дефолтный формат
    }
}

