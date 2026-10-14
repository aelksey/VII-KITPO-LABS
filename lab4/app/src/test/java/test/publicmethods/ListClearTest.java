package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListClearTest {

    private CustomList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(3);
        list.add(10);
        list.add(20);
        list.add(30);
    }

    @Test
    @DisplayName("Should completely reset list metrics and unlink all nodes")
    public void testClearStandardBehavior() {
        assertTrue(list.getSize() > 0);

        list.clear();

        assertEquals(0, list.getSize(), "Size must be 0 after clear");
        assertEquals(0, list.getNodeCount(), "Node count must be 0 after clear");
    }

    @Test
    @DisplayName("Boundary Case: Calling clear on an already empty list should be safe")
    public void testClearEmptyList() {
        list.clear(); // Первый вызов
        
        // Повторный вызов на пустую структуру
        assertDoesNotThrow(() -> list.clear(), "Calling clear on an empty list must not crash or throw exceptions");
        assertEquals(0, list.getSize());
    }
}
