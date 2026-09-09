package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import core.CustomList;
import types.IntegerStrategy;
import util.TestDataGenerator;

public class TestDataGeneratorTest {

    private IntegerStrategy intStrategy;

    @BeforeEach
    public void setUp() {
        intStrategy = new IntegerStrategy();
    }

    @Test
    public void testGenerateWithFixedDepth() {
        int maxListSize = 10;
        int maxDepth = 4;

        // Генерируем с ФИКСИРОВАННОЙ глубиной (useRandomDepth = false)
        CustomList<Integer> list = TestDataGenerator.generateRandomList(intStrategy, maxListSize, maxDepth, false);

        assertNotNull(list);
        assertTrue(list.getSize() > 0);
        assertTrue(list.getSize() <= maxListSize);
    }

    @Test
    public void testGenerateWithRandomDepthOption() {
        int maxListSize = 20;
        int maxDepth = 6;

        // Генерируем со СЛУЧАЙНОЙ глубиной (useRandomDepth = true)
        // Метод должен успешно отработать для каждого элемента, не выходя за пределы maxDepth
        assertDoesNotThrow(() -> {
            CustomList<Integer> list = TestDataGenerator.generateRandomList(intStrategy, maxListSize, maxDepth, true);
            
            assertNotNull(list);
            assertTrue(list.getSize() > 0, "Список должен быть заполнен элементами");
            assertTrue(list.getSize() <= maxListSize, "Размер не должен превышать максимальный лимит");
        }, "Генерация со случайной глубиной ячеек не должна выбрасывать исключений");
    }
}