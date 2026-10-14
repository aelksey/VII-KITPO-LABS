package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListSetBlockCapacityTest {

    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(2);
        list.add("A");
        list.add("B");
        list.add("C");
        list.add("D"); // 2 ноды
    }

    @Test
    @DisplayName("Should shrink node layout when expanding capacity")
    public void testSetBlockCapacityExpansion() {
        // Увеличиваем вместимость до 5. Все 4 элемента теперь умещаются в один блок.
        list.setBlockCapacity(5);

        assertEquals(5, list.getBlockCapacity());
        assertEquals(1, list.getNodeCount(), "Node layout should condense into 1 node");
        assertEquals("C", list.get(2), "Data layout must preserve integrity after capacity changes");
    }

    @Test
    @DisplayName("Should expand node layout when shrinking capacity")
    public void testSetBlockCapacityShrinkage() {
        // Уменьшаем вместимость до 1. Каждый элемент переносится в свою индивидуальную ноду.
        list.setBlockCapacity(1);

        assertEquals(1, list.getBlockCapacity());
        assertEquals(4, list.getNodeCount(), "Node count must grow to match the number of single-cell blocks");
        assertEquals("D", list.get(3));
    }

    @Test
    @DisplayName("Boundary Case: Reject invalid capacity inputs and handle unchanged capacity skips")
    public void testSetBlockCapacityBoundaries() {
        // Передача той же емкости, что уже установлена (должен быть моментальный skip без перестройки)
        list.setBlockCapacity(2);
        assertEquals(2, list.getNodeCount());

        // Негативные и нулевые граничные значения
        assertThrows(IllegalArgumentException.class, () -> list.setBlockCapacity(0), "Zero capacity should be rejected");
        assertThrows(IllegalArgumentException.class, () -> list.setBlockCapacity(-5), "Negative capacity should be rejected");
    }
}
