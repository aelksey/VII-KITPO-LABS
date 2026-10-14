package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListInsertTest {

    private CustomList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(3); // Вместимость блока = 3
    }

    @Test
    @DisplayName("Should shift elements to the right inside a partially filled block")
    public void testInsertStandardShift() {
        list.add(10);
        list.add(30);

        list.insert(1, 20); // Вставка между 10 и 30

        assertEquals(3, list.getSize(), "Size must increment to 3");
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1), "Inserted value should be at index 1");
        assertEquals(30, list.get(2), "Old value should be shifted to index 2");
    }

    @Test
    @DisplayName("Should trigger cascade shift (carry) to a new node when the block is full")
    public void testInsertWithCascadeCarry() {
        list.add(10);
        list.add(20);
        list.add(30); // Блок полностью заполнен (размер = 3)
        assertEquals(1, list.getNodeCount());

        list.insert(1, 15); // Вставка вызовет перенос числа 30 в новый блок

        assertEquals(4, list.getSize());
        assertEquals(2, list.getNodeCount(), "A new internal node should be allocated for the carried element");
        assertEquals(15, list.get(1));
        assertEquals(20, list.get(2));
        assertEquals(30, list.get(3), "The last element must be pushed into the next node");
    }

    @Test
    @DisplayName("Boundary Case: Should handle insertion at the very beginning and at the very end")
    public void testInsertBoundaries() {
        // Вставка в пустой список (индекс 0)
        list.insert(0, 100);
        assertEquals(100, list.get(0));

        // Вставка в позицию, равную текущему размеру (эквивалентно add)
        list.insert(1, 200);
        assertEquals(200, list.get(1));

        // Ошибочные граничные значения
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(-1, 500), "Negative index should fail");
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(3, 500), "Index greater than size should fail");
    }
}
