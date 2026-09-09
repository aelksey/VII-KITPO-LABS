package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import types.DoubleStrategy;
import types.StringStrategy;

public class UserTypesTest {

    @Test
    public void testDoubleStrategyHappyPathAndEdgeCases() {
        DoubleStrategy strategy = new DoubleStrategy();

        // Базовые методы
        assertEquals("Вещественное число", strategy.typeName());
        assertEquals(0.0, strategy.create());
        assertEquals(5.5, strategy.clone(5.5));

        // Парсинг
        assertEquals(3.14, strategy.parseValue("3.14"));
        assertEquals(-10.5, strategy.parseValue("  -10.5  ")); // проверка trim()

        // Сравнение
        assertTrue(strategy.compare(1.5, 2.5) < 0);
        assertTrue(strategy.compare(5.0, 5.0) == 0);
        assertTrue(strategy.compare(0.0, -1.0) > 0);
    }

    @Test
    public void testStringStrategyHappyPathAndEdgeCases() {
        StringStrategy strategy = new StringStrategy();

        // Базовые методы
        assertEquals("Строка", strategy.typeName());
        assertEquals("", strategy.create());
        assertEquals("Тест", strategy.clone("Тест"));

        // Парсинг
        assertEquals("Привет", strategy.parseValue("  Привет  "));

        // Сравнение (лексикографическое)
        assertTrue(strategy.compare("Apple", "Banana") < 0);
        assertTrue(strategy.compare("Тест", "Тест") == 0);
        
        // Edge cases со значениями null в сравнении
        assertTrue(strategy.compare(null, "Не null") < 0);
        assertTrue(strategy.compare("Не null", null) > 0);
        assertTrue(strategy.compare(null, null) == 0);
    }
}