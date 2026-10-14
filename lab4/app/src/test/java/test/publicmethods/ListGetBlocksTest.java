package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ListGetBlocksTest {

    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(3); // Вместимость блока = 3
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4"); // Всего 4 элемента -> распределятся на 2 блока (3 в первом, 1 во втором)
    }

    @Test
    @DisplayName("Should partition data values into immutable chunks mirroring physical node layout matrix")
    public void testGetBlocksPartitioning() {
        List<List<String>> matrix = list.getBlocks();

        assertNotNull(matrix);
        assertEquals(2, matrix.size(), "Matrix rows must match internal node allocation records");
        assertEquals(3, matrix.get(0).size(), "First block chunk size must match its fill factor limit");
        assertEquals(1, matrix.get(1).size(), "Second block chunk size must capture overflow left-overs");
        assertEquals("4", matrix.get(1).get(0));
    }

    @Test
    @DisplayName("Boundary Case: Blocks matrix returned from empty layouts must be empty")
    public void testGetBlocksEmpty() {
        list.clear();

        List<List<String>> matrix = list.getBlocks();

        assertNotNull(matrix);
        assertTrue(matrix.isEmpty(), "Zero allocated nodes inside collection structure should present 0 block entries");
    }
}
