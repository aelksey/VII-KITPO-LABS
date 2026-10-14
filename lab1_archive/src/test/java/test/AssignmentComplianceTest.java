package test;

import core.CustomList;
import factory.SerializeFactory;
import factory.SortFactory;
import factory.UserFactory;
import gui.ListVisualizer;
import inface.UserTypeInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import types.IntegerStrategy;
import types.StringStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class AssignmentComplianceTest {
    @TempDir Path directory;


    @Test void firstThatStopsAtFirstMatchAndArrayIsIndependent() {
        CustomList<Integer> list = new CustomList<>();
        list.add(3); list.add(7); list.add(9);
        AtomicInteger calls = new AtomicInteger();
        assertEquals(7, list.firstThat(v -> { calls.incrementAndGet(); return v > 5; }));
        assertEquals(2, calls.get());
        assertNull(list.firstThat(v -> v < 0));
        list.toArrayList().clear();
        assertEquals(3, list.getSize());
    }

    @Test void bothSortsHandleLargeOrderedReverseAndEqualInputsWithinComparisonBound() {
        int size = 20000;
        for (String name : SortFactory.getStrategyNameList()) {
            for (int pattern = 0; pattern < 3; pattern++) {
                CustomList<Integer> list = new CustomList<>();
                for (int i = 0; i < size; i++) list.add(pattern == 0 ? i : pattern == 1 ? size - i : 7);
                AtomicInteger comparisons = new AtomicInteger();
                list.sort(SortFactory.getStrategyByName(name), (Integer a, Integer b) -> {
                    comparisons.incrementAndGet(); return Integer.compare(a, b);
                });
                var values = list.toArrayList();
                assertEquals(size, values.size());
                for (int i = 1; i < size; i++) assertTrue(values.get(i - 1) <= values.get(i));
                assertTrue(comparisons.get() < 4 * size * 15, name + ": " + comparisons.get());
            }
        }
    }

    @Test void sortingPreservesDataIdentityAndBlockCapacity() {
        CustomList<String> list = new CustomList<>();
        String b = new String("B"), a = new String("A");
        list.add(b); list.add(a);
        list.sort(new sort.MergeSortStrategy(), new StringStrategy().getTypeComparator());
        assertSame(a, list.get(0)); assertSame(b, list.get(1));
        assertEquals(3, list.getBlockCapacity());
        list.add("C"); assertEquals(3, list.getSize());
    }

    @Test void everyRegisteredTypeRoundTripsInEveryFormat() throws Exception {
        for (String name : UserFactory.getTypeNameList()) roundTrip(UserFactory.getBuilderByName(name));
    }

    private <T> void roundTrip(UserTypeInterface<T> type) throws Exception {
        CustomList<T> list = new CustomList<>();
        type.sampleValues().forEach(value -> list.add(type.clone(value)));
        for (String format : SerializeFactory.getFormatNameList()) {
            var serializer = SerializeFactory.getStrategyByName(format);
            Path file = directory.resolve("values.data");
            serializer.save(file.toString(), list, type);
            CustomList<T> restored = new CustomList<>();
            restored.add(type.create());
            serializer.load(file.toString(), restored, type);
            assertEquals(list.getSize(), restored.getSize(), format);
            for (int i = 0; i < list.getSize(); i++)
                assertEquals(type.serializeValue(list.get(i)), type.serializeValue(restored.get(i)), format);
            list.sort(new sort.MergeSortStrategy(), type.getTypeComparator());
        }
    }

    @Test void stringsIncludingWhitespaceAndControlCharactersRoundTrip() throws Exception {
        StringStrategy type = new StringStrategy();
        CustomList<String> list = new CustomList<>();
        List<String> values = List.of("", "  текст  ", "кавычка\" и \\ путь", "\n\r\t\b\f\u0001");
        values.forEach(list::add);
        for (String format : SerializeFactory.getFormatNameList()) {
            var serializer = SerializeFactory.getStrategyByName(format);
            Path file = directory.resolve("strings.data");
            serializer.save(file.toString(), list, type);
            CustomList<String> restored = new CustomList<>();
            serializer.load(file.toString(), restored, type);
            assertEquals(values, restored.toArrayList(), format);
        }
    }

    @Test void malformedFilesDoNotDestroyExistingData() throws Exception {
        CustomList<Integer> list = new CustomList<>(); list.add(123);
        Path json = directory.resolve("bad.json");
        var serializer = new serialize.JsonSerializeStrategy();
        for (String malformed : List.of("[\"1\",]", "[\"1\"] extra", "[\"oops\"]", "[\"bad\\q\"]", "[1]")) {
            Files.writeString(json, malformed);
            assertThrows(IOException.class, () -> serializer.load(json.toString(), list, new IntegerStrategy()));
            assertEquals(List.of(123), list.toArrayList());
        }
        Path binary = directory.resolve("bad.bin");
        Files.write(binary, new byte[]{0, 0, 0, 2});
        assertThrows(IOException.class, () -> new serialize.BinarySerializeStrategy().load(binary.toString(), list, new IntegerStrategy()));
        assertEquals(List.of(123), list.toArrayList());
    }

    @Test void compactJsonAndUnicodeEscapesAreAccepted() throws Exception {
        Path json = directory.resolve("compact.json");
        Files.writeString(json, "[\"\\u0410\",\"a\\/b\"]");
        CustomList<String> list = new CustomList<>();
        new serialize.JsonSerializeStrategy().load(json.toString(), list, new StringStrategy());
        assertEquals(List.of("А", "a/b"), list.toArrayList());
    }

    @Test void emptyFilesReplaceExistingListsForEveryFormat() throws Exception {
        for (String format : SerializeFactory.getFormatNameList()) {
            var serializer = SerializeFactory.getStrategyByName(format);
            Path file = directory.resolve("empty.data");
            serializer.save(file.toString(), new CustomList<Integer>(), new IntegerStrategy());
            CustomList<Integer> list = new CustomList<>(); list.add(1);
            serializer.load(file.toString(), list, new IntegerStrategy());
            assertEquals(0, list.getSize());
        }
    }

    @Test void newTypeNeedsNoChangesToListSortSerializationOrGenerator() throws Exception {
        // Этот тип существует только в тесте: прикладные компоненты его не знают.
        UserTypeInterface<Long> type = new UserTypeInterface<>() {
            public String typeName() { return "Длинное целое"; }
            public Long create() { return 9L; }
            public Long randomValue(java.util.random.RandomGenerator random) { return 123L; }
            public Long clone(Long value) { return value; }
            public Long readValue(InputStreamReader in) { return create(); }
            public Long parseValue(String text) { return Long.valueOf(text); }
            public int compare(Long a, Long b) { return Long.compare(a, b); }
            public String toString(Long value) { return "Отображение: " + value; }
            public void writeValue(Long value, BufferedWriter writer) throws IOException { writer.write(value.toString()); }
        };
        roundTrip(type);
        assertEquals(123L, util.TestDataGenerator.generateRandomList(type, 1, 1, false).get(0));
    }

    @Test void visualizationPaintsPublicSnapshotWithoutOpeningWindow() throws Exception {
        javax.swing.SwingUtilities.invokeAndWait(() -> {
            CustomList<Integer> list = new CustomList<>(); list.add(1); list.add(2);

            ListVisualizer<Integer> view = new ListVisualizer<>(list, new IntegerStrategy());
            view.setSize(view.getPreferredSize());
            var image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_INT_RGB);
            var graphics = image.createGraphics();
            try { assertDoesNotThrow(() -> view.paint(graphics)); } finally { graphics.dispose(); }
        });
    }
}

