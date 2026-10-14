package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import core.CustomList;
import model.Point2D;
import inface.UserTypeInterface;
import inface.SortStrategyInterface;
import factory.UserFactory;
import factory.SortFactory;

public class ListSortTest {
    private UserTypeInterface<Point2D> pointBuilder;
    private UserTypeInterface<Integer> intBuilder;
    private SortStrategyInterface quickSort;
    private SortStrategyInterface mergeSort;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {
        pointBuilder = (UserTypeInterface<Point2D>) UserFactory.getBuilderByName(new types.Point2DStrategy().typeName());
        intBuilder = (UserTypeInterface<Integer>) UserFactory.getBuilderByName("Целое число");
        quickSort = SortFactory.getStrategyByName(new sort.QuickSortStrategy().strategyName());
        mergeSort = SortFactory.getStrategyByName(new sort.MergeSortStrategy().strategyName());
    }

        @Test
    public void testIntegerQuickSort() {
        CustomList<Integer> intList = new CustomList<>();
        intList.add(intBuilder.parseValue("42"));
        intList.add(intBuilder.parseValue("7"));
        intList.add(intBuilder.parseValue("-15"));
        intList.add(intBuilder.parseValue("100"));

        intList.sort(quickSort, intBuilder);

        assertEquals(4, intList.getSize());
        assertEquals(-15, intList.get(0));
        assertEquals(7, intList.get(1));
        assertEquals(42, intList.get(2));
        assertEquals(100, intList.get(3));
    }

    @Test
    public void testPoint2DMergeSort() {
        CustomList<Point2D> pointList = new CustomList<>();
        // Сортировка идет по расстоянию от (0,0)
        pointList.add(pointBuilder.parseValue("3.0 4.0"));   // 5.0
        pointList.add(pointBuilder.parseValue("1.0 1.0"));   // ~1.41
        pointList.add(pointBuilder.parseValue("5.0 12.0"));  // 13.0

        pointList.sort(mergeSort, pointBuilder);

        assertEquals(3, pointList.getSize());
        // Первым должен быть элемент с наименьшим расстоянием (1.0 1.0)
        assertEquals("Точка(1,00; 1,00) [Дист: 1,41]", pointBuilder.toString(pointList.get(0)));
        assertEquals("Точка(3,00; 4,00) [Дист: 5,00]", pointBuilder.toString(pointList.get(1)));
        assertEquals("Точка(5,00; 12,00) [Дист: 13,00]", pointBuilder.toString(pointList.get(2)));
    }

    @Test
    public void testSortEmptyAndSingleElement() {
        CustomList<Integer> emptyList = new CustomList<>();
        // Сортировка пустого списка не должна падать
        assertDoesNotThrow(() -> emptyList.sort(quickSort, intBuilder));

        CustomList<Integer> singleList = new CustomList<>();
        singleList.add(10);
        // Сортировка списка из 1 элемента не должна падать
        assertDoesNotThrow(() -> singleList.sort(quickSort, intBuilder));
        assertEquals(10, singleList.get(0));
    }
}

