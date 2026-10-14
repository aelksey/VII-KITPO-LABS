package test.java.test.publicmethods;

import core.CustomList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.*;

public class ListFirstThatTest {

    private CustomList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>(3);
        list.add(5);
        list.add(12);
        list.add(19);
        list.add(26);
    }

    @Test
    @DisplayName("Should successfully return the first item matching the predicate filter condition")
    public void testFirstThatStandardMatch() {
        // Поиск первого четного числа
        Predicate<Integer> isEven = (Integer x) -> x % 2 == 0;
        
        Integer result = list.firstThat(isEven);
        
        assertNotNull(result);
        assertEquals(12, result, "Should fetch 12 since it's the first even item encountered");
    }

    @Test
    @DisplayName("Boundary Case: Return null if no matching elements are found or if the list is empty")
    public void testFirstThatNoMatchBoundaries() {
        // Условие, под которое элементы не подходят
        Predicate<Integer> isNegative = (Integer x) -> x < 0;
        assertNull(list.firstThat(isNegative), "Must return null when there are no matches");

        // Поиск по пустому списку
        list.clear();
        assertNull(list.firstThat((Integer x) -> true), "Must return null when executing search over empty data sets");

        // Передача null вместо предиката
        assertThrows(NullPointerException.class, () -> list.firstThat(null), "Null predicate query should throw NPE");
    }
}
