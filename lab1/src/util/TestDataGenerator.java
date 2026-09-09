package util;

import java.util.Random;
import core.CustomList;
import inface.UserTypeInterface;

/**
 * Вспомогательный класс для генерации списков со случайными данными.
 */
public class TestDataGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Генерирует CustomList со случайными значениями, настраиваемой длиной и глубиной массивов ссылок.
     */
    public static <T> CustomList<T> generateRandomList(UserTypeInterface<T> typeStrategy, 
                                                       int maxListSize, 
                                                       int maxDepth, 
                                                       boolean useRandomDepth) {
        CustomList<T> list = new CustomList<>();
        
        // 1. Задаем случайную длину списка в пределах от 1 до maxListSize
        int actualSize = RANDOM.nextInt(maxListSize) + 1;

        // 2. Заполняем ячейки списка случайными данными базового типа.
        // Метод add() выстраивает правильную, неразрывную цепочку по уровню next[0].
        for (int i = 0; i < actualSize; i++) {
            T randomValue = generateValueForStrategy(typeStrategy);
            list.add(randomValue);
        }

        // 3. Настройка дополнительных уровней (высоты) массивов ссылок next в ячейках
        // Важно: maxDepth должен быть хотя бы больше 1, чтобы были дополнительные уровни, помимо базового нулевого.
        if (maxDepth > 1 && actualSize > 0) {
            for (int i = 0; i < actualSize; i++) {
                
                // Вычисляем целевую глубину для текущей ячейки (минимум 2 уровня, максимум maxDepth)
                int targetDepth = useRandomDepth ? (RANDOM.nextInt(maxDepth - 1) + 2) : maxDepth;

                // Устанавливаем пустую (-1 -> null) ссылку на самом верхнем уровне (targetDepth - 1).
                // Уровень гарантированно >= 1, поэтому мы НЕ затрем базовый служебный уровень next[0].
                list.setLinkAtLevel(i, targetDepth - 1, -1);

                // Случайным образом связываем ячейки между собой на промежуточных уровнях
                // Начинаем строго с уровня l = 1, чтобы не сломать линейную связность списка на уровне 0
                for (int level = 1; level < targetDepth - 1; level++) {
                    if (RANDOM.nextBoolean() && actualSize > 1) {
                        int targetIdx = RANDOM.nextInt(actualSize);
                        
                        // Безопасная проверка: устанавливаем ссылку, только если целевой индекс валиден
                        list.setLinkAtLevel(i, level, targetIdx);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Внутренний метод генерации случайной строки-значения для парсинга стратегией типа.
     */
    private static <T> T generateValueForStrategy(UserTypeInterface<T> typeStrategy) {
        String typeName = typeStrategy.typeName();

        switch (typeName) {
            case "Целое число":
                int randomInt = RANDOM.nextInt(2001) - 1000;
                return typeStrategy.parseValue(String.valueOf(randomInt));

            case "Вещественное число":
                double randomDouble = -1000.0 + (2000.0 * RANDOM.nextDouble());
                String formattedDouble = String.format(java.util.Locale.US, "%.2f", randomDouble);
                return typeStrategy.parseValue(formattedDouble);

            case "Строка":
                int length = RANDOM.nextInt(6) + 4;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char randomChar = (char) ('A' + RANDOM.nextInt(26));
                    sb.append(randomChar);
                }
                return typeStrategy.parseValue(sb.toString());

            default:
                return typeStrategy.create();
        }
    }
}