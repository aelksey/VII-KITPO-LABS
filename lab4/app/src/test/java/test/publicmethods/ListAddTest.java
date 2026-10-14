package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListAddTest {

    private CustomList<Integer> list;

    @BeforeEach
    public void setUp() {
        // Задаем небольшую вместимость блока 3, чтобы легко тестировать создание новых нод
        list = new CustomList<>(3);
    }

    @Test
    @DisplayName("Should successfully add single element and increment size")
    public void testAddSingleElement() {
        list.add(42);
        
        assertEquals(1, list.getSize(), "List size should be 1 after adding one element");
        assertEquals(1, list.getNodeCount(), "Node count should be 1 for a single element");
        assertEquals(42, list.get(0), "The element at index 0 must be 42");
    }

    @Test
    @DisplayName("Should allocate a new internal node when block capacity is exceeded")
    public void testAddTriggersNewNodeAllocation() {
        list.add(10);
        list.add(20);
        list.add(30); // Первый блок памяти (Node) полностью заполнен
        
        assertEquals(1, list.getNodeCount(), "Node count must be 1 when block is exactly full");
        
        list.add(40); // Этот элемент должен вызвать аллокацию второго блока памяти
        
        assertEquals(4, list.getSize(), "Total list size should be 4");
        assertEquals(2, list.getNodeCount(), "Node count must increment to 2 after exceeding block capacity");
        assertEquals(40, list.get(3), "The fourth element must be accessible at index 3");
    }

    @Test
    @DisplayName("Boundary Case: Should correctly add edge values like null and extreme numbers")
    public void testAddBoundaryValues() {
        // Проверка работы с экстремальными значениями
        list.add(Integer.MIN_VALUE);
        list.add(Integer.MAX_VALUE);
        
        assertEquals(Integer.MIN_VALUE, list.get(0));
        assertEquals(Integer.MAX_VALUE, list.get(1));

        // Проверка поддержки null-значений, если они не запрещены логикой
        list.add(null);
        assertEquals(3, list.getSize());
        assertNull(list.get(2), "The list should allow storing null values safely");
    }
}
