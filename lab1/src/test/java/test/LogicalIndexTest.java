package test;

import core.CustomList;
import inface.TraverseStrategyInterface;
import org.junit.jupiter.api.Test;
import traverse.LinearTraverseStrategy;
import traverse.ReverseTraverseStrategy;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LogicalIndexTest {
    private CustomList<String> list() {
        var list = new CustomList<String>();
        list.add("A"); list.add("B"); list.add("C");
        return list;
    }
    private List<String> order(CustomList<String> list, TraverseStrategyInterface<String> traversal) {
        var result = new ArrayList<String>(); traversal.traverse(list, result::add); return result;
    }

    @Test void insertAtEveryLogicalPositionMatchesTraversalSequence() {
        for (TraverseStrategyInterface<String> traversal : List.<TraverseStrategyInterface<String>>of(
                new LinearTraverseStrategy<>(), new ReverseTraverseStrategy<>())) {
            for (int index = 0; index <= 3; index++) {
                var list = list();
                var expected = new ArrayList<>(order(list, traversal));
                expected.add(index, "X");
                list.insert(index, "X", traversal);
                assertEquals(expected, order(list, traversal));
                assertEquals("X", list.get(index, traversal));
                list.remove(index, traversal);
                assertEquals(List.of("A", "B", "C"), list.toArrayList());
            }
        }
    }

    @Test void reverseAppendPrependAndGetUseTraversalOrder() {
        var list = list(); var reverse = new ReverseTraverseStrategy<String>();
        assertEquals("C", list.get(0, reverse));
        list.add("D", reverse);
        assertEquals(List.of("C", "B", "A", "D"), order(list, reverse));
        list.insert(0, "E", reverse);
        assertEquals(List.of("E", "C", "B", "A", "D"), order(list, reverse));
        list.remove(0, reverse); list.remove(list.getSize() - 1, reverse);
        assertEquals(List.of("A", "B", "C"), list.toArrayList());
    }

    @Test void emptySingletonAndInvalidIndices() {
        var list = new CustomList<String>(); var reverse = new ReverseTraverseStrategy<String>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0, reverse));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(1, "X", reverse));
        list.add("X", reverse);
        assertEquals("X", list.get(0, reverse));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1, reverse));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1, reverse));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(-1, "Y", reverse));
        list.remove(0, reverse);
        assertEquals(0, list.getSize());
        list.add("Z"); assertEquals(List.of("Z"), list.toArrayList());
    }


    @Test void searchAndDisplayedIndicesAgreeWithTraversal() {
        var list = list(); var reverse = new ReverseTraverseStrategy<String>();
        assertEquals("C", list.firstThat(v -> true, reverse));
        assertEquals("A", list.firstThat(v -> true, new LinearTraverseStrategy<>()));
        assertNull(list.firstThat(v -> false, reverse));
        for (int logical = 0; logical < 3; logical++)
            assertEquals(logical, reverse.logicalIndex(reverse.elementIndex(logical, 3), 3));
    }
}

