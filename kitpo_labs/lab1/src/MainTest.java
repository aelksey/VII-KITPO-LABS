import core.CustomList;
import inface.ForEachCallbackInterface;
import inface.UserTypeInterface;
import model.Point2D;
import inface.SortStrategyInterface;
import inface.SerializeStrategyInterface;
import factory.UserFactory;
import factory.SortFactory;
import factory.SerializeFactory;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("=== СТАРТ ТЕСТИРОВАНИЯ СД «Список с динам. массивом ссылок» ===\n");

        // ===============================================================
        // 1. Базовые операции: add, insert, get, remove, forEach
        // ===============================================================
        System.out.println(">>> 1. Базовые операции (тип: 2D-Точка)");

        @SuppressWarnings("unchecked")
        UserTypeInterface<Point2D> pointBuilder =
            (UserTypeInterface<Point2D>) UserFactory.getBuilderByName("2D-Точка (по расстоянию)");

        CustomList<Point2D> pointList = new CustomList<>();

        pointList.add(pointBuilder.parseValue("3.0 4.0"));   // 5.0
        pointList.add(pointBuilder.parseValue("1.0 1.0"));   // 1.41
        pointList.add(pointBuilder.parseValue("0.0 2.0"));   // 2.0
        pointList.add(pointBuilder.parseValue("5.0 12.0"));  // 13.0
        pointList.insert(2, pointBuilder.parseValue("0.0 0.0")); // индекс 2

        System.out.println("Исходный список:");
        pointList.forEach(new ForEachCallbackInterface<Point2D>() {
        @Override
        public void toDo(Point2D p) {
            System.out.println("  - " + pointBuilder.toString(p));
        }});

        // Проверка get
        System.out.println("\nget(0) = " + pointBuilder.toString(pointList.get(0)));
        System.out.println("get(2) = " + pointBuilder.toString(pointList.get(2)));
        System.out.println("getSize() = " + pointList.getSize());

        // Удаление
        pointList.remove(1);
        System.out.println("\nПосле remove(1):");
         pointList.forEach(new ForEachCallbackInterface<Point2D>() {
        @Override
        public void toDo(Point2D p) {
            System.out.println("  - " + pointBuilder.toString(p));
        }});
        System.out.println("getSize() = " + pointList.getSize());

        // ===============================================================
        // 2. Демонстрация динамического массива ссылок: setLinkAtLevel
        // ===============================================================
        System.out.println("\n>>> 2. Динамический массив ссылок (setLinkAtLevel)");

        CustomList<Point2D> linkList = new CustomList<>();
        linkList.add(pointBuilder.parseValue("1.0 0.0"));  // индекс 0
        linkList.add(pointBuilder.parseValue("2.0 0.0"));  // индекс 1
        linkList.add(pointBuilder.parseValue("3.0 0.0"));  // индекс 2
        linkList.add(pointBuilder.parseValue("4.0 0.0"));  // индекс 3

        linkList.setLinkAtLevel(0, 1, 2);
        System.out.println("setLinkAtLevel(0, 1, 2) — элемент 0, уровень 1 -> элемент 2");

        linkList.setLinkAtLevel(2, 1, 3);
        System.out.println("setLinkAtLevel(2, 1, 3) — элемент 2, уровень 1 -> элемент 3");

        linkList.setLinkAtLevel(0, 2, 3);
        System.out.println("setLinkAtLevel(0, 2, 3) — элемент 0, уровень 2 -> элемент 3");

        linkList.setLinkAtLevel(1, 3, -1);
        System.out.println("setLinkAtLevel(1, 3, -1) — элемент 1, уровень 3 -> null");

        System.out.println("\nСтруктура после установки ссылок:");
        linkList.printStructure(pointBuilder);

        // ===============================================================
        // 3. Сортировка (стратегии меняются на лету)
        // ===============================================================
        System.out.println("\n>>> 3. Сортировка (стратегии на лету)");

        System.out.println("Список перед сортировкой:");
         pointList.forEach(new ForEachCallbackInterface<Point2D>() {
        @Override
        public void toDo(Point2D p) {
            System.out.println("  - " + pointBuilder.toString(p));
        }});

        SortStrategyInterface quick = SortFactory.getStrategyByName("Быстрая сортировка (QuickSort)");
        pointList.sort(quick, pointBuilder);
        System.out.println("\nПосле QuickSort:");
        
        pointList.forEach(new ForEachCallbackInterface<Point2D>() {
        @Override
        public void toDo(Point2D p) {
            System.out.println("  - " + pointBuilder.toString(p));
        }});

        pointList.add(pointBuilder.parseValue("1.0 0.0")); // расстояние 1.0
        SortStrategyInterface merge = SortFactory.getStrategyByName("Сортировка слиянием (MergeSort)");
        pointList.sort(merge, pointBuilder);
        System.out.println("\nПосле добавления и MergeSort:");
        pointList.forEach(new ForEachCallbackInterface<Point2D>() {
        @Override
        public void toDo(Point2D p) {
            System.out.println("  - " + pointBuilder.toString(p));
        }});

        System.out.println("\nСтруктура после сортировки:");
        pointList.printStructure(pointBuilder);

        // ===============================================================
        // 4. Сериализация (стратегии меняются на лету)
        // ===============================================================
        System.out.println("\n>>> 4. Сериализация");

        String txtFile = "points.txt";
        String binFile = "points.bin";

        try {
            SerializeStrategyInterface txtStrategy =
                SerializeFactory.getStrategyByName("Текстовый формат (Plain Text / JSON)");
            txtStrategy.save(txtFile, pointList, pointBuilder);
            System.out.println(" [OK] Сохранено в текст: " + txtFile);

            SerializeStrategyInterface binStrategy =
                SerializeFactory.getStrategyByName("Собственный двоичный формат (.bin)");
            binStrategy.save(binFile, pointList, pointBuilder);
            System.out.println(" [OK] Сохранено в бинарный: " + binFile);

            CustomList<Point2D> restored = new CustomList<>();
            binStrategy.load(binFile, restored, pointBuilder);

            System.out.println("\nВосстановлено из бинарного:");
            restored.forEach(p -> System.out.println("  -> " + pointBuilder.toString(p)));

        } catch (Exception e) {
            System.err.println("Ошибка сериализации: " + e.getMessage());
            e.printStackTrace();
        }

        // ===============================================================
        // 5. Переключение на другой тип данных (Integer)
        // ===============================================================
        System.out.println("\n-------------------------------------------------------------");
        System.out.println(">>> 5. Переключение на Integer");

        @SuppressWarnings("unchecked")
        UserTypeInterface<Integer> intBuilder =
            (UserTypeInterface<Integer>) UserFactory.getBuilderByName("Целое число");
        CustomList<Integer> intList = new CustomList<>();

        intList.add(intBuilder.parseValue("42"));
        intList.add(intBuilder.parseValue("7"));
        intList.add(intBuilder.parseValue("-15"));
        intList.add(intBuilder.parseValue("100"));

        System.out.println("Исходный список чисел:");
        intList.forEach(i -> System.out.println("  - " + intBuilder.toString(i)));

        intList.sort(
            SortFactory.getStrategyByName("Быстрая сортировка (QuickSort)"),
            intBuilder
        );
        System.out.println("После QuickSort:");
        intList.forEach(i -> System.out.println("  - " + intBuilder.toString(i)));

        intList.setLinkAtLevel(0, 1, 3);
        intList.setLinkAtLevel(1, 2, 3);
        System.out.println("\nСтруктура чисел со ссылками:");
        intList.printStructure(intBuilder);

        // ===============================================================
        // 6. Граничные случаи
        // ===============================================================
        System.out.println("\n>>> 6. Граничные случаи");

        // Пустой список
        CustomList<Point2D> emptyList = new CustomList<>();
        System.out.println("Пустой список, getSize() = " + emptyList.getSize());
        emptyList.printStructure(pointBuilder);

        // Один элемент
        CustomList<Point2D> singleList = new CustomList<>();
        singleList.add(pointBuilder.parseValue("9.0 12.0"));
        System.out.println("\nОдин элемент, getSize() = " + singleList.getSize());
        singleList.printStructure(pointBuilder);
        singleList.remove(0);
        System.out.println("После remove(0), getSize() = " + singleList.getSize());

        // Очистка
        pointList.clear();
        System.out.println("\nПосле clear(), getSize() = " + pointList.getSize());
        pointList.printStructure(pointBuilder);

        System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }
}
