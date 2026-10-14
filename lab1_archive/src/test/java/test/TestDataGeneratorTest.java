package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import core.CustomList;
import types.IntegerStrategy;
import util.TestDataGenerator;

public class TestDataGeneratorTest {

    private IntegerStrategy intStrategy;

    @Test
    void randomValuesHaveExpectedRangesAndAreNotLimitedToSamples() {
        var random = new java.util.Random(42);
        var integers = new java.util.HashSet<Integer>();
        var doubles = new java.util.HashSet<Double>();
        var strings = new java.util.HashSet<String>();
        var points = new java.util.HashSet<String>();
        var integerType = new IntegerStrategy();
        var doubleType = new types.DoubleStrategy();
        var stringType = new types.StringStrategy();
        var pointType = new types.Point2DStrategy();
        for (int i = 0; i < 100; i++) {
            int integer = integerType.randomValue(random);
            assertTrue(integer >= -1000 && integer <= 1000);
            integers.add(integer);
            double decimal = doubleType.randomValue(random);
            assertTrue(decimal >= -1000 && decimal <= 1000);
            doubles.add(decimal);
            String text = stringType.randomValue(random);
            assertTrue(text.matches("[a-zA-Z0-9]{3,12}"));
            strings.add(text);
            var point = pointType.randomValue(random);
            assertTrue(point.getX() >= -100 && point.getX() <= 100);
            assertTrue(point.getY() >= -100 && point.getY() <= 100);
            points.add(point.getX() + " " + point.getY());
        }
        // Фиксированное зерно делает проверку разнообразия воспроизводимой.
        assertTrue(integers.size() > 6);
        assertTrue(doubles.size() > 6);
        assertTrue(strings.size() > 6);
        assertTrue(points.size() > 6);
    }

    @BeforeEach
    public void setUp() {
        intStrategy = new IntegerStrategy();
    }

    @Test
    public void testGenerateWithFixedCapacity() {
        int maxListSize = 10;
        int maxBlockCapacity = 4;

        // Генерируем с ФИКСИРОВАННЫМ размером массива (randomCapacity = false)
        CustomList<Integer> list = TestDataGenerator.generateRandomList(intStrategy, maxListSize, maxBlockCapacity, false);

        assertNotNull(list);
        assertTrue(list.getSize() > 0);
        assertTrue(list.getSize() <= maxListSize);
    }

    @Test
    public void testGenerateWithRandomCapacityOption() {
        int maxListSize = 20;
        int maxBlockCapacity = 6;

        // Генерируем со СЛУЧАЙНЫМ размером массива (randomCapacity = true)
        // Метод должен успешно отработать для каждого элемента, не выходя за пределы maxBlockCapacity
        assertDoesNotThrow(() -> {
            CustomList<Integer> list = TestDataGenerator.generateRandomList(intStrategy, maxListSize, maxBlockCapacity, true);
            
            assertNotNull(list);
            assertTrue(list.getSize() > 0, "Список должен быть заполнен элементами");
            assertTrue(list.getSize() <= maxListSize, "Размер не должен превышать максимальный лимит");
        }, "Генерация со случайным размером массивов не должна выбрасывать исключений");
    }
}
