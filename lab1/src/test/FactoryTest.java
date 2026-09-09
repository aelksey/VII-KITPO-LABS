package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import factory.UserFactory;
import inface.UserTypeInterface;

public class FactoryTest {

    @Test
    public void testUserFactoryReturnsCorrectBuilders() {
        UserTypeInterface<?> intBuilder = UserFactory.getBuilderByName("Целое число");
        assertNotNull(intBuilder, "Фабрика чисел не должна возвращать null");

        UserTypeInterface<?> pointBuilder = UserFactory.getBuilderByName("2D-Точка (по расстоянию)");
        assertNotNull(pointBuilder, "Фабрика точек не должна возвращать null");
    }

    @Test
    public void testBuilderParsingInvalidDataThrowsException() {
        UserTypeInterface<?> intBuilder = UserFactory.getBuilderByName("Целое число");
        
        // Передача букв вместо числа должна приводить к ошибке парсинга
        assertThrows(Exception.class, () -> {
            intBuilder.parseValue("строка_вместо_числа");
        });
    }
}
