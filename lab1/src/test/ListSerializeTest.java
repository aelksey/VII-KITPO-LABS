package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import core.CustomList;
import model.Point2D;
import inface.UserTypeInterface;
import inface.SerializeStrategyInterface;
import factory.UserFactory;
import factory.SerializeFactory;

public class ListSerializeTest {
    private UserTypeInterface<Point2D> pointBuilder;
    private CustomList<Point2D> originalList;
    private final String TXT_FILE = "test_points.txt";
    private final String BIN_FILE = "test_points.bin";

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {
        pointBuilder = (UserTypeInterface<Point2D>) UserFactory.getBuilderByName("2D-Точка (по расстоянию)");
        originalList = new CustomList<>();
        originalList.add(pointBuilder.parseValue("3.0 4.0"));
        originalList.add(pointBuilder.parseValue("1.0 1.0"));
    }
    @Test
    public void testBinarySerializationAndDeserialization() {
        try {
            SerializeStrategyInterface binStrategy = SerializeFactory.getStrategyByName("Собственный двоичный формат (.bin)");
            
            // 1. Сохраняем
            binStrategy.save(BIN_FILE, originalList, pointBuilder);
            File file = new File(BIN_FILE);
            assertTrue(file.exists(), "Бинарный файл должен быть создан");

            // 2. Восстанавливаем в новый список
            CustomList<Point2D> restoredList = new CustomList<>();
            binStrategy.load(BIN_FILE, restoredList, pointBuilder);

            // 3. Проверяем идентичность данных
            assertEquals(originalList.getSize(), restoredList.getSize());
            assertEquals(pointBuilder.toString(originalList.get(0)), pointBuilder.toString(restoredList.get(0)));
            assertEquals(pointBuilder.toString(originalList.get(1)), pointBuilder.toString(restoredList.get(1)));

            // Чистим за собой файл
            file.delete();
        } catch (Exception e) {
            fail("Сериализация выбросила исключение: " + e.getMessage());
        }
    }

    @Test
    public void testTextSerialization() {
        try {
            SerializeStrategyInterface txtStrategy = SerializeFactory.getStrategyByName("Текстовый формат (Plain Text / JSON)");
            txtStrategy.save(TXT_FILE, originalList, pointBuilder);
            
            File file = new File(TXT_FILE);
            assertTrue(file.exists(), "Текстовый файл должен быть создан");
            
            file.delete(); // Удаляем временный файл
        } catch (Exception e) {
            fail("Текстовая сериализация сломалась: " + e.getMessage());
        }
    }
}
