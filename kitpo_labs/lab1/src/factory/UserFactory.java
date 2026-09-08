package factory;

import java.util.ArrayList;
import java.util.List;

import inface.UserTypeInterface;
import types.*;

// Фабрика типов
public class UserFactory {
    private static final List<UserTypeInterface<?>> builders = new ArrayList<>();

    static {
        // Регистрация типов данных. 
        // Для добавления нового ТД — просто допишите сюда его экземпляр!
        builders.add(new Point2DStrategy());
        builders.add(new IntegerStrategy());
    }

    public static List<String> getTypeNameList() {
        List<String> names = new ArrayList<>();
        for (UserTypeInterface<?> b : builders) {
            names.add(b.typeName());
        }
        return names;
    }

    public static UserTypeInterface<?> getBuilderByName(String name) {
        for (UserTypeInterface<?> b : builders) {
            if (b.typeName().equals(name)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Тип не найден: " + name);
    }
}

