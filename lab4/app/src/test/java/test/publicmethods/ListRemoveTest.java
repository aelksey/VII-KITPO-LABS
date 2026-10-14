package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListRemoveTest {

    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(2); // Вместимость блока = 2
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D"); // 2 ноды по 2 элемента
    }

    @Test
    @DisplayName("Should remove element from the middle and shift remaining items left")
    public void testRemoveStandardShift() {
        list.remove(1); // Удаляем "B"

        assertEquals(3, list.getSize());
        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1), "Elements should pull back left after deletion");
        assertEquals("D", list.get(2));
    }

    @Test
    @DisplayName("Should deallocate trailing node when it becomes empty after removal")
    public void testRemoveTriggersNodeCleanup() {
        assertEquals(2, list.getNodeCount());

        list.remove(3); // Удаляем "D"
        list.remove(2); // Удаляем "C" -> вторая нода становится полностью пустой

        assertEquals(2, list.getSize());
        assertEquals(1, list.getNodeCount(), "The empty trailing node must be completely unlinked");
    }

    @Test
    @DisplayName("Boundary Case: Should handle removing head, tail, and only element left")
    public void testRemoveBoundaries() {
        // Удаление первого элемента (head)
        list.remove(0);
        assertEquals("B", list.get(0));

        // Удаление последнего элемента (tail)
        list.remove(list.getSize() - 1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));

        // Ошибочные граничные индексы
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.getSize()));
    }
}
