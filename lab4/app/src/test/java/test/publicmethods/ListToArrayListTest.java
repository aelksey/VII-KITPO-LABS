package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class ListToArrayListTest {

    private CustomList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(3);
        list.add(100);
        list.add(200);
    }

    @Test
    @DisplayName("Should successfully convert state to a flat standard Java ArrayList collection")
    public void testToArrayListExport() {
        ArrayList<Integer> exportedList = list.toArrayList();

        assertNotNull(exportedList);
        assertEquals(list.getSize(), exportedList.size(), "Exported array size must map the origin size metrics");
        assertEquals(100, exportedList.get(0));
        assertEquals(200, exportedList.get(1));
    }

    @Test
    @DisplayName("Boundary Case: Exporting an empty list must produce a valid empty ArrayList")
    public void testToArrayListEmpty() {
        list.clear();

        ArrayList<Integer> exportedList = list.toArrayList();

        assertNotNull(exportedList);
        assertTrue(exportedList.isEmpty(), "Exporting an empty collection state should return an empty standard layout");
    }
}
