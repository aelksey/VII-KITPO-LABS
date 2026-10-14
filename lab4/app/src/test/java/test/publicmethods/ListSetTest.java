package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListSetTest {

    private CustomList<Double> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(2);
        list.add(1.1);
        list.add(2.2);
        list.add(3.3);
    }

    @Test
    @DisplayName("Should successfully replace value at given index without changing list size")
    public void testSetValidIndex() {
        int initialSize = list.getSize();
        
        list.set(1, 9.9); // Меняем элемент 2.2 на 9.9 в первом блоке
        list.set(2, 8.8); // Меняем элемент 3.3 на 8.8 во втором блоке
        
        assertEquals(initialSize, list.getSize(), "The total list size must remain unchanged after set()");
        assertEquals(9.9, list.get(1), "Value at index 1 was not modified correctly");
        assertEquals(8.8, list.get(2), "Value at index 2 was not modified correctly");
    }

    @Test
    @DisplayName("Boundary Case: Should handle set() operations at edge positions and detect out of bounds")
    public void testSetBoundaryScenarios() {
        // Изменение самого первого элемента (граничный индекс 0)
        list.set(0, 0.0);
        assertEquals(0.0, list.get(0));

        // Изменение самого последнего элемента (граничный индекс size - 1)
        list.set(list.getSize() - 1, 5.5);
        assertEquals(5.5, list.get(list.getSize() - 1));

        // Проверка генерации исключений при неверных индексах
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 3.14));
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(list.getSize(), 3.14));
    }
}
