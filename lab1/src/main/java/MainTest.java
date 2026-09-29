import core.CustomList;
import factory.SerializeFactory;
import factory.SortFactory;
import factory.UserFactory;
import inface.ForEachCallbackInterface;
import inface.UserTypeInterface;
import java.nio.file.Files;
import java.nio.file.Path;

//5Один сценарий для каждого прототипа из фабрики
public class MainTest {
    public static void main(String[] args) throws Exception {
        Path directory = Path.of("target", "demo");
        Files.createDirectories(directory);
        int index = 0;
        for (String name : UserFactory.getTypeNameList())
            demonstrate(UserFactory.getBuilderByName(name), directory.resolve("type-" + index++));
        System.out.println("Все типы: демонстрация завершена успешно");
    }

    private static <T> void demonstrate(UserTypeInterface<T> type, Path prefix) throws Exception {
        System.out.println("\nТип: " + type.typeName());
        CustomList<T> list = new CustomList<>(2);
        for (T sample : type.sampleValues()) list.add(type.clone(type.parseValue(type.serializeValue(sample))));
        T first = list.get(0);
        list.insert(1, type.clone(first));
        list.remove(1);
        System.out.println("Узлов-массивов: " + list.getNodeCount() + ", ячеек в узле: " + list.getBlockCapacity());
        list.forEach(new ForEachCallbackInterface<T>() {
            @Override public void toDo(T value) { System.out.println("  " + type.toString(value)); }
        });
        System.out.println("firstThat: " + type.toString(list.firstThat(v -> type.compare(v, first) == 0)));
        for (String sort : SortFactory.getStrategyNameList()) {
            list.sort(SortFactory.getStrategyByName(sort), type.getTypeComparator());
            System.out.println(sort);
            list.forEach(v -> System.out.println("  " + type.toString(v)));
        }
        int formatIndex = 0;
        for (String format : SerializeFactory.getFormatNameList()) {
            var serializer = SerializeFactory.getStrategyByName(format);
            String filename = prefix + "-format-" + formatIndex++ + ".data";
            serializer.save(filename, list, type);
            CustomList<T> restored = new CustomList<>();
            restored.add(type.clone(first)); // загрузка должна заменить старые данные
            serializer.load(filename, restored, type);
            if (list.getSize() != restored.getSize()) throw new IllegalStateException("Размер после загрузки");
            for (int i = 0; i < list.getSize(); i++)
                if (!type.serializeValue(list.get(i)).equals(type.serializeValue(restored.get(i))))
                    throw new IllegalStateException("Данные после загрузки: " + format);
            System.out.println("Сохранение/загрузка OK: " + format);
        }
        list.clear();
        System.out.println("clear: размер = " + list.getSize());
    }
}
