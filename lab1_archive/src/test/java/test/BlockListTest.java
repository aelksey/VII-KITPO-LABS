package test;

import core.CustomList;
import factory.SerializeFactory;
import factory.UserFactory;
import inface.TraverseStrategyInterface;
import inface.UserTypeInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import traverse.LinearTraverseStrategy;
import traverse.ReverseTraverseStrategy;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class BlockListTest {
    @TempDir Path directory;

    @Test void eachNodeStoresSeveralReferencesAndLastNodeMayBePartial() {
        CustomList<Integer> list = new CustomList<>(3);
        for (int i = 0; i < 8; i++) list.add(i);
        assertEquals(8, list.getSize()); assertEquals(3, list.getNodeCount());
        assertEquals(List.of(List.of(0, 1, 2), List.of(3, 4, 5), List.of(6, 7)), list.getBlocks());
        list.insert(3, 99);
        assertEquals(List.of(List.of(0, 1, 2), List.of(99, 3, 4), List.of(5, 6, 7)), list.getBlocks());
        list.remove(2);
        assertEquals(List.of(List.of(0, 1, 99), List.of(3, 4, 5), List.of(6, 7)), list.getBlocks());
    }

    @Test void referencesSurviveRegroupingAndSortingWithoutCloningObjects() {
        CustomList<StringBuilder> list = new CustomList<>(2);
        StringBuilder a = new StringBuilder("aaa"), b = new StringBuilder("b");
        list.add(a); list.add(b); list.add(a);
        assertSame(a, list.getBlocks().get(1).get(0));
        a.append("!"); assertEquals("aaa!", list.get(0).toString());
        list.setBlockCapacity(3);
        assertEquals(1, list.getNodeCount()); assertSame(a, list.getBlocks().get(0).get(2));
        list.sort(new sort.MergeSortStrategy(), Comparator.comparingInt(StringBuilder::length));
        assertSame(b, list.get(0)); assertSame(a, list.get(1)); assertSame(a, list.get(2));
        assertThrows(UnsupportedOperationException.class, () -> list.getBlocks().clear());
        assertThrows(UnsupportedOperationException.class, () -> list.getBlocks().get(0).clear());
    }

    @Test void removalReleasesEmptyTailAndListCanBeReused() {
        for (int capacity : List.of(1, 2, 3, 7)) {
            CustomList<Integer> list = new CustomList<>(capacity);
            for (int i = 0; i < 10; i++) list.add(i);
            while (list.getSize() > 0) { list.remove(0); checkPacking(list); }
            assertEquals(0, list.getNodeCount()); assertTrue(list.getBlocks().isEmpty());
            list.add(42); assertEquals(42, list.get(0));
            list.remove(0); list.insert(0, 7); assertEquals(7, list.get(0));
            list.clear(); assertEquals(capacity, list.getBlockCapacity());
        }
    }

    private static void checkPacking(CustomList<?> list) {
        assertEquals((list.getSize() + list.getBlockCapacity() - 1) / list.getBlockCapacity(), list.getNodeCount());
        var blocks = list.getBlocks();
        int size = 0;
        for (int i = 0; i < blocks.size(); i++) {
            assertFalse(blocks.get(i).isEmpty());
            assertTrue(blocks.get(i).size() <= list.getBlockCapacity());
            if (i + 1 < blocks.size()) assertEquals(list.getBlockCapacity(), blocks.get(i).size());
            size += blocks.get(i).size();
        }
        assertEquals(list.getSize(), size);
    }

    @Test void randomizedOperationsMatchFlatSequenceAcrossBlocksAndBothTraversals() {
        for (int capacity : List.of(1, 2, 3, 7)) {
            for (TraverseStrategyInterface<Integer> traversal : List.<TraverseStrategyInterface<Integer>>of(
                    new LinearTraverseStrategy<>(), new ReverseTraverseStrategy<>())) {
                Random random = new Random(724);
                CustomList<Integer> list = new CustomList<>(capacity);
                ArrayList<Integer> expected = new ArrayList<>();
                for (int operation = 0; operation < 600; operation++) {
                    int choice = expected.isEmpty() ? 0 : random.nextInt(4);
                    int value = random.nextInt(10); // повторяющиеся значения
                    if (choice == 0) { list.add(value, traversal); expected.add(value); }
                    else if (choice == 1) {
                        int index = random.nextInt(expected.size() + 1);
                        list.insert(index, value, traversal); expected.add(index, value);
                    } else if (choice == 2) {
                        int index = random.nextInt(expected.size());
                        assertEquals(expected.get(index), list.get(index, traversal));
                        list.remove(index, traversal); expected.remove(index);
                    } else list.setBlockCapacity(1 + random.nextInt(7));
                    ArrayList<Integer> actual = new ArrayList<>();
                    traversal.traverse(list, actual::add);
                    assertEquals(expected, actual); checkPacking(list);
                }
            }
        }
    }

    @Test void invalidInputsDoNotChangeBlocks() {
        assertThrows(IllegalArgumentException.class, () -> new CustomList<>(0));
        var list = new CustomList<String>(2); list.add("A");
        assertThrows(IllegalArgumentException.class, () -> list.setBlockCapacity(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(2, "B"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertEquals(List.of(List.of("A")), list.getBlocks());
    }

    @Test void allTypesAndFormatsReconstructBlocksAtSelectedCapacity() throws Exception {
        for (String name : UserFactory.getTypeNameList()) roundTrip(UserFactory.getBuilderByName(name));
    }

    @Test void shippedFilesMatchGuiSamplesAndFillMultipleNodes() throws Exception {
        checkSampleFiles("integers", new types.IntegerStrategy());
        checkSampleFiles("doubles", new types.DoubleStrategy());
        checkSampleFiles("strings", new types.StringStrategy());
        checkSampleFiles("points", new types.Point2DStrategy());
    }

    private <T> void checkSampleFiles(String name, UserTypeInterface<T> type) throws Exception {
        var samples = type.sampleValues();
        assertEquals(6, samples.size());
        for (var serializer : List.<inface.SerializeStrategyInterface>of(
                new serialize.PlainTxtSerializeStrategy(), new serialize.JsonSerializeStrategy())) {
            String extension = serializer.fileExtension();
            String file = Path.of("files", extension, name + "." + extension).toString();
            for (int capacity : List.of(3, 16)) {
                var list = new CustomList<T>(capacity);
                serializer.load(file, list, type);
                assertEquals(6, list.getSize());
                assertEquals(capacity == 3 ? 2 : 1, list.getNodeCount());
                for (int i = 0; i < samples.size(); i++)
                    assertEquals(type.serializeValue(samples.get(i)), type.serializeValue(list.get(i)), file);
            }
        }
    }
    private <T> void roundTrip(UserTypeInterface<T> type) throws Exception {
        var list = new CustomList<T>(2);
        for (int repeat = 0; repeat < 3; repeat++) type.sampleValues().forEach(v -> list.add(type.clone(v)));
        assertTrue(list.getNodeCount() > 1);
        for (String format : SerializeFactory.getFormatNameList()) {
            var serializer = SerializeFactory.getStrategyByName(format);
            String file = directory.resolve("values." + serializer.fileExtension()).toString();
            serializer.save(file, list, type);
            for (int capacity : List.of(2, 4)) {
                var restored = new CustomList<T>(capacity); restored.add(type.create());
                serializer.load(file, restored, type);
                assertEquals(capacity, restored.getBlockCapacity());
                assertEquals(list.getSize(), restored.getSize());
                for (int i = 0; i < list.getSize(); i++)
                    assertEquals(type.serializeValue(list.get(i)), type.serializeValue(restored.get(i)));
                checkPacking(restored);
            }
        }
    }
}

