package test.java.test.publicmethods;

import core.CustomList;
import inface.ForEachCallbackInterface;
import inface.TraverseStrategyInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.*;

public class ListFirstIndexThatTest {

    private CustomList<Integer> list;
    private TraverseStrategyInterface<Integer> linearStrategy;

    // Изолированная именованная заглушка для обхода проблемы стирания типов (Raw Type)
    private static class MockLinearTraversal implements TraverseStrategyInterface<Integer> {
        @Override
        public void traverse(CustomList<Integer> list, ForEachCallbackInterface<Integer> callback) {
            list.forEach(callback);
        }
    }

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(2);
        list.add(15);
        list.add(30);
        list.add(45);
        linearStrategy = new MockLinearTraversal();
    }

    @Test
    @DisplayName("Should return accurate logical index of the first item meeting predicate criteria")
    public void testFirstIndexThatValidMatch() {
        Predicate<Integer> isDivisibleByTen = (Integer x) -> x % 10 == 0;

        int foundIndex = list.firstIndexThat(isDivisibleByTen, linearStrategy);

        assertEquals(1, foundIndex, "Index of 30 should be exactly 1");
    }

    @Test
    @DisplayName("Boundary Case: Should return -1 when matches are absent from the dataset structure")
    public void testFirstIndexThatNoMatch() {
        Predicate<Integer> isNegative = (Integer x) -> x < 0;

        int foundIndex = list.firstIndexThat(isNegative, linearStrategy);

        assertEquals(-1, foundIndex, "Should return -1 if predicate constraints filter out all entries");
    }
}
