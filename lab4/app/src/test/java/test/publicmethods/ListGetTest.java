package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListGetTest {

    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(2); // Вместимость блока = 2
        list.add("A");
        list.add("B");
        list.add("C"); // Переходит в следующий блок
    }

    @Test
    @DisplayName("Should retrieve correct elements from different internal nodes")
    public void testGetValidIndices() {
        // Чтение из первого блока памяти
        assertEquals("A", list.get(0), "Failed to retrieve element from first node at offset 0");
        assertEquals("B", list.get(1), "Failed to retrieve element from first node at offset 1");
        
        // Чтение из второго блока памяти (проверка работы кэширования blockAt)
        assertEquals("C", list.get(2), "Failed to retrieve element from second node");
    }

    @Test
    @DisplayName("Boundary Case: Should throw IndexOutOfBoundsException for invalid indices")
    public void testGetInvalidIndices() {
        // Краевой случай 1: Отрицательный индекс
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1), 
            "Should throw IndexOutOfBoundsException for negative index");

        // Краевой случай 2: Индекс, равный размеру списка (get(size))
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.getSize()), 
            "Should throw IndexOutOfBoundsException when index equals size");

        // Краевой случай 3: Индекс далеко за пределами размера списка
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(999), 
            "Should throw IndexOutOfBoundsException for out of bounds index");
    }
}
