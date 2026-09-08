package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import core.CustomList;
import factory.SerializeFactory;
import factory.UserFactory;
import inface.SerializeStrategyInterface;
import inface.UserTypeInterface;

public class JsonSerializeTest {
    private SerializeStrategyInterface jsonStrategy;
    private UserTypeInterface<Integer> intBuilder;
    private final String TEST_JSON_FILE = "test_output.json";

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {
        // Получаем стратегию из вашей фабрики
        jsonStrategy = SerializeFactory.getStrategyByName("(JSON)");
        
        // Получаем строитель типов для работы с целыми числами
        intBuilder = (UserTypeInterface<Integer>) UserFactory.getBuilderByName("Целое число");
        
        // Гарантируем, что перед началом каждого теста файла на диске нет
        File file = new File(TEST_JSON_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testFormatName() {
        // Проверяем, что метод formatName возвращает правильную строку для GUI
        assertNotNull(jsonStrategy, "Стратегия JSON должна быть успешно создана фабрикой");
        assertEquals("(JSON)", jsonStrategy.formatName(), 
            "Имя формата должно строго соответствовать строке для выпадающего списка");
    }

    @Test
    public void testJsonSaveAndLoadStandard() throws IOException {
        CustomList<Integer> originalList = new CustomList<>();
        originalList.add(42);
        originalList.add(-7);
        originalList.add(100);

        // 1. Сохраняем данные в JSON-файл
        jsonStrategy.save(TEST_JSON_FILE, originalList, intBuilder);
        
        File file = new File(TEST_JSON_FILE);
        assertTrue(file.exists(), "JSON-файл должен физически создаться на диске");

        // 2. Считываем данные в новый, чистый список
        CustomList<Integer> restoredList = new CustomList<>();
        jsonStrategy.load(TEST_JSON_FILE, restoredList, intBuilder);

        // 3. Проверяем корректность восстановления
        assertEquals(originalList.getSize(), restoredList.getSize(), "Размер списка должен совпадать");
        assertEquals(42, restoredList.get(0));
        assertEquals(-7, restoredList.get(1));
        assertEquals(100, restoredList.get(2));

        // Чистим за собой временный файл
        file.delete();
    }

    @Test
    public void testJsonEmptyListEdgeCase() throws IOException {
        CustomList<Integer> emptyList = new CustomList<>();

        // Сохраняем пустой список (должен записаться пустой массив `[\n]`)
        jsonStrategy.save(TEST_JSON_FILE, emptyList, intBuilder);
        
        CustomList<Integer> restoredList = new CustomList<>();
        // Добавляем фейковый элемент, чтобы проверить, что метод load() очищает список перед загрузкой
        restoredList.add(999); 
        
        jsonStrategy.load(TEST_JSON_FILE, restoredList, intBuilder);

        // Проверяем граничный случай
        assertEquals(0, restoredList.getSize(), "При загрузке пустого JSON-файла список должен очищаться до 0");
        
        new File(TEST_JSON_FILE).delete();
    }

    @Test
    public void testLoadNonExistentFile() throws IOException {
        CustomList<Integer> list = new CustomList<>();
        list.add(123);

        // Попытка загрузить несуществующий файл
        String fakeFile = "this_file_does_not_exist_12345.json";
        
        // Согласно логике вашей стратегии, это не должно вызывать ошибку, а просто оставить список пустым/очистить его
        jsonStrategy.load(fakeFile, list, intBuilder);

        assertEquals(0, list.getSize(), "Загрузка из несуществующего файла должна сбрасывать/очищать список");
    }
}
