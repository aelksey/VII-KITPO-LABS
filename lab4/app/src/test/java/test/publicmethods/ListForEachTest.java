package test.java.test.publicmethods;

import core.CustomList;
import inface.ForEachCallbackInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ListForEachTest {

    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(2);
        list.add("Alpha");
        list.add("Beta");
    }

    @Test
    @DisplayName("Should touch every data value sequentially executing callback logic")
    public void testForEachIteration() {
        List<String> sink = new ArrayList<>();
        ForEachCallbackInterface<String> action = sink::add;

        list.forEach(action);

        assertEquals(2, sink.size(), "Callback should be fired exactly twice");
        assertEquals("Alpha", sink.get(0));
        assertEquals("Beta", sink.get(1));
    }

    @Test
    @DisplayName("Boundary Case: Throw NullPointerException for null action reference")
    public void testForEachBoundaries() {
        assertThrows(NullPointerException.class, () -> list.forEach(null), "Invoking with a null callback reference must fail");
    }
}
