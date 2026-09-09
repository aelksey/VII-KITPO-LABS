package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import core.CustomList;

public class CustomListTest {

    private CustomList<String> list;

    @BeforeEach
    public void setUp() {
        list = new CustomList<>();
    }

    @Test
    public void testAddAndGet() {
        list.add("Первый");
        list.add("Второй");
        list.add("Третий");

        assertEquals(3, list.getSize());
        assertEquals("Первый", list.get(0));
        assertEquals("Второй", list.get(1));
        assertEquals("Третий", list.get(2));
    }

    @Test
    public void testInsertInMiddle() {
        list.add("Элемент 1");
        list.add("Элемент 3");
        list.insert(1, "Элемент 2");

        assertEquals(3, list.getSize());
        assertEquals("Элемент 1", list.get(0));
        assertEquals("Элемент 2", list.get(1));
        assertEquals("Элемент 3", list.get(2));
    }

    @Test
    public void testRemoveFromMiddle() {
        list.add("А");
        list.add("Б");
        list.add("В");
        list.remove(1);

        assertEquals(2, list.getSize());
        assertEquals("А", list.get(0));
        assertEquals("В", list.get(1));
    }

    @Test
    public void testClear() {
        list.add("Тест 1");
        list.clear();
        assertEquals(0, list.getSize());
    }

    @Test
    public void testForEach() {
        list.add("A");
        list.add("B");
        StringBuilder result = new StringBuilder();
        list.forEach(data -> result.append(data));
        assertEquals("AB", result.toString());
    }

    @Test
    public void testMultiLevelLinks() {
        list.add("Узел 0");
        list.add("Узел 1");
        list.add("Узел 2");
        list.setLinkAtLevel(0, 3, 2); 
        assertEquals(3, list.getSize());
    }

        @Test
    public void testGetOnEmptyListThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(0);
        });
    }

    @Test
    public void testGetWithNegativeIndexThrowsException() {
        list.add("Данные");
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(-1);
        });
    }

    @Test
    public void testInsertAtBeginningOfEmptyList() {
        list.insert(0, "Начало");
        assertEquals(1, list.getSize());
        assertEquals("Начало", list.get(0));
    }

    @Test
    public void testInsertAtTheVeryEnd() {
        list.add("А");
        list.insert(1, "Б");
        assertEquals(2, list.getSize());
        assertEquals("Б", list.get(1));
    }

    @Test
    public void testRemoveFirstElement() {
        list.add("Удалить");
        list.add("Оставить");
        list.remove(0);
        assertEquals(1, list.getSize());
        assertEquals("Оставить", list.get(0));
    }

    @Test
    public void testRemoveLastElement() {
        list.add("Оставить");
        list.add("Удалить");
        list.remove(1);
        assertEquals(1, list.getSize());
    }

    @Test
    public void testSetLinkAtLevelWithTargetIndexMinusOne() {
        list.add("Узел 0");
        list.add("Узел 1");
        list.setLinkAtLevel(0, 1, 1);
        list.setLinkAtLevel(0, 1, -1);
        assertEquals(2, list.getSize());
    }

    @Test
    public void testSetLinkAtLevelInvalidIndices() {
        list.add("Узел 0");
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.setLinkAtLevel(5, 0, 0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.setLinkAtLevel(0, 0, 5);
        });
    }
}
