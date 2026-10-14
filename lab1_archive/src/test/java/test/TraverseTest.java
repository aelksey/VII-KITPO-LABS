package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import core.CustomList;
import factory.TraverseFactory;
import inface.TraverseStrategyInterface;
import java.util.ArrayList;
import java.util.List;

public class TraverseTest {
    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>();
        list.add("A");
        list.add("B");
        list.add("C");
    }
    @SuppressWarnings("unchecked")
    @Test
    public void testLinearTraverseStrategy() {
        TraverseStrategyInterface<String> strategy = TraverseFactory.getStrategyByName("Линейный обход");
        assertNotNull(strategy, "Фабрика должна вернуть линейную стратегию");

        List<String> result = new ArrayList<>();
        strategy.traverse(list, result::add);

        assertEquals(3, result.size());
        assertEquals("A", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("C", result.get(2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReverseTraverseStrategy() {
        TraverseStrategyInterface<String> strategy = TraverseFactory.getStrategyByName("Обратный обход");
        assertNotNull(strategy, "Фабрика должна вернуть обратную стратегию");

        List<String> result = new ArrayList<>();
        strategy.traverse(list, result::add);

        assertEquals(3, result.size());
        assertEquals("C", result.get(0));
        assertEquals("B", result.get(1));
        assertEquals("A", result.get(2));
    }

    @Test
    public void testFactoryReturnsNullOnUnknown() {
        assertNull(TraverseFactory.getStrategyByName("НесуществующийОбход"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTraverseEmptyList() {
        CustomList<String> emptyList = new CustomList<>();
        TraverseStrategyInterface<String> strategy = TraverseFactory.getStrategyByName("Обратный обход");
        
        List<String> result = new ArrayList<>();
        // Обход пустого списка не должен вызывать ошибок
        assertDoesNotThrow(() -> strategy.traverse(emptyList, result::add));
        assertTrue(result.isEmpty());
    }
}
