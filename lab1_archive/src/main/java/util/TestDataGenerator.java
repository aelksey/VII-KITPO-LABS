package util;

import core.CustomList;
import inface.UserTypeInterface;
import java.util.Random;

// Генерирует объекты через прототип и упаковывает ссылки в массивы узлов
public class TestDataGenerator {
    private static final Random RANDOM = new Random();

    public static <T> CustomList<T> generateRandomList(UserTypeInterface<T> type,
            int maxListSize, int maxBlockCapacity, boolean randomCapacity) {
        if (maxListSize < 1 || maxBlockCapacity < 1)
            throw new IllegalArgumentException("Размер списка и массива должны быть положительными");
        int capacity = randomCapacity ? RANDOM.nextInt(maxBlockCapacity) + 1 : maxBlockCapacity;
        CustomList<T> list = new CustomList<>(capacity);
        int size = RANDOM.nextInt(maxListSize) + 1;
        for (int i = 0; i < size; i++) list.add(type.randomValue(RANDOM));
        return list;
    }
}
