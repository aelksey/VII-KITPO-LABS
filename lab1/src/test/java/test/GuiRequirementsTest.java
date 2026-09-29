package test;

import factory.SerializeFactory;
import factory.UserFactory;
import gui.ListApplicationPanel;
import gui.SerializationFileDialog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GuiRequirementsTest {
    @TempDir Path directory;

    private static List<Component> descendants(Container container) {
        List<Component> result = new ArrayList<>();
        for (Component child : container.getComponents()) {
            result.add(child);
            if (child instanceof Container nested) result.addAll(descendants(nested));
        }
        return result;
    }
    private static Component named(Container root, String name) {
        return descendants(root).stream().filter(c -> name.equals(c.getName())).findFirst().orElseThrow();
    }
    private static void click(Container root, String label) {
        ((JButton) descendants(root).stream().filter(c -> c instanceof JButton b && label.equals(b.getText()))
                .findFirst().orElseThrow()).doClick();
    }
    private static String output(Container root) { return ((JTextArea)named(root, "output")).getText().trim(); }

    private static void insertSixValues(Container app) {
        for (int i = 0; i < 6; i++) click(app, "Вставить в конец");
    }

    @Test void controlsOperateOnListAndSelectedTraversalChangesOutput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            ((JComboBox<?>)named(app, "dataType")).setSelectedItem("Целое число");
            JTextField value = (JTextField)named(app, "value");
            value.setText("1"); click(app, "Вставить в конец");
            value.setText("2"); click(app, "Вставить в начало");
            value.setText("3"); ((JSpinner)named(app, "logicalIndex")).setValue(1);
            click(app, "Вставить по номеру");
            click(app, "Обойти"); assertEquals("2\n3\n1", output(app));
            ((JComboBox<?>)named(app, "traversal")).setSelectedItem("Обратный обход");
            click(app, "Обойти"); assertEquals("1\n3\n2", output(app));
            click(app, "Получить"); assertEquals("3", output(app));
            click(app, "Удалить"); click(app, "Обойти"); assertEquals("1\n2", output(app));
            click(app, "Очистить"); click(app, "Обойти"); assertEquals("Список пуст", output(app));
        });
    }

    @Test void reverseOperationsUseLogicalNumbersAfterSwitchingStrategy() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            ((JComboBox<?>)named(app, "dataType")).setSelectedItem("Целое число");
            JTextField value = (JTextField)named(app, "value");
            for (String number : List.of("1", "2", "3")) {
                value.setText(number); click(app, "Вставить в конец");
            }
            var traversal = (JComboBox<?>)named(app, "traversal");
            traversal.setSelectedItem("Обратный обход");
            click(app, "Получить"); assertEquals("3", output(app));
            ((JSpinner)named(app, "logicalIndex")).setValue(1);
            value.setText("9"); click(app, "Вставить по номеру");
            click(app, "Обойти"); assertEquals("3\n9\n2\n1", output(app));
            ((JSpinner)named(app, "logicalIndex")).setValue(2); click(app, "Удалить");
            value.setText("4"); click(app, "Вставить в конец");
            value.setText("5"); click(app, "Вставить в начало");
            click(app, "Обойти"); assertEquals("5\n3\n9\n1\n4", output(app));
            traversal.setSelectedItem("Линейный обход");
            click(app, "Обойти"); assertEquals("4\n1\n9\n3\n5", output(app));
        });
    }

    @Test void searchShowsFirstLogicalIndexForSelectedTraversal() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            ((JComboBox<?>)named(app, "dataType")).setSelectedItem("Целое число");
            JTextField value = (JTextField)named(app, "value");
            value.setText("7"); click(app, "Найти"); assertEquals("Не найдено", output(app));
            for (String number : List.of("7", "2", "7", "9")) {
                value.setText(number); click(app, "Вставить в конец");
            }
            value.setText("7"); click(app, "Найти");
            assertEquals("Логический номер: 0\nЗначение: 7", output(app));
            ((JComboBox<?>)named(app, "traversal")).setSelectedItem("Обратный обход");
            click(app, "Найти");
            assertEquals("Логический номер: 1\nЗначение: 7", output(app));
            value.setText("100"); click(app, "Найти"); assertEquals("Не найдено", output(app));
        });
    }

    @Test void topSelectorsRemainAvailableWhenDataTypeChanges() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            var traversal = (JComboBox<?>)named(app, "traversal");
            var serialization = (JComboBox<?>)named(app, "serialization");
            traversal.setSelectedItem("Обратный обход");
            serialization.setSelectedItem(new serialize.JsonSerializeStrategy().formatName());
            for (String name : UserFactory.getTypeNameList()) {
                ((JComboBox<?>)named(app, "dataType")).setSelectedItem(name);
                click(app, "Обойти"); assertEquals("Список пуст", output(app));
                insertSixValues(app); click(app, "Обойти");
                assertEquals("Объектов: 6   Узлов-массивов: 2", ((JLabel)named(app, "blockStatus")).getText());
                assertFalse(output(app).isBlank());
                assertEquals("Обратный обход", traversal.getSelectedItem());
                assertEquals(new serialize.JsonSerializeStrategy().formatName(), serialization.getSelectedItem());
            }
        });
    }

    @Test void everyFormatFiltersAndValidatesFileExtensions() throws Exception {
        for (String name : SerializeFactory.getFormatNameList()) {
            var strategy = SerializeFactory.getStrategyByName(name);
            String extension = strategy.fileExtension();
            SwingUtilities.invokeAndWait(() -> {
                JFileChooser chooser = SerializationFileDialog.createChooser(strategy);
                assertFalse(chooser.isAcceptAllFileFilterUsed());
                assertTrue(chooser.getFileFilter().accept(new File("list." + extension)));
                assertFalse(chooser.getFileFilter().accept(new File("list.wrong")));
            });
            File resolved = SerializationFileDialog.resolveSelection(directory.resolve("list").toFile(), strategy, true);
            assertEquals("list." + extension, resolved.getName());
            assertThrows(IOException.class, () -> SerializationFileDialog.resolveSelection(directory.resolve("list.wrong").toFile(), strategy, true));
            assertThrows(IOException.class, () -> SerializationFileDialog.resolveSelection(directory.resolve("missing." + extension).toFile(), strategy, false));
            File existing = Files.createFile(directory.resolve("list." + extension.toUpperCase(java.util.Locale.ROOT))).toFile();
            assertEquals(existing, SerializationFileDialog.resolveSelection(existing, strategy, false));
        }
    }

    @Test void regroupingPreservesValuesAndUpdatesNodeCount() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            ((JComboBox<?>)named(app, "dataType")).setSelectedItem("Целое число");
            insertSixValues(app);
            assertEquals("Объектов: 6   Узлов-массивов: 2", ((JLabel)named(app, "blockStatus")).getText());
            click(app, "Обойти"); String before = output(app);
            ((JSpinner)named(app, "blockCapacity")).setValue(2);
            click(app, "Перегруппировать");
            assertEquals("Объектов: 6   Узлов-массивов: 3", ((JLabel)named(app, "blockStatus")).getText());
            click(app, "Обойти"); assertEquals(before, output(app));
        });
    }

    @Test void randomGenerationReplacesListForEveryTypeAndPreservesCapacity() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            for (String name : UserFactory.getTypeNameList()) {
                ((JComboBox<?>)named(app, "dataType")).setSelectedItem(name);
                ((JComboBox<?>)named(app, "traversal")).setSelectedItem("Обратный обход");
                ((JSpinner)named(app, "blockCapacity")).setValue(2);
                click(app, "Перегруппировать");
                insertSixValues(app);
                // Введённый вручную максимум должен примениться при нажатии кнопки.
                JSpinner maxSize = (JSpinner)named(app, "randomMaxSize");
                ((JSpinner.DefaultEditor)maxSize.getEditor()).getTextField().setText("1");
                click(app, "Случайный список");
                assertEquals("Создан случайный список. Элементов: 1", output(app));
                assertEquals("Объектов: 1   Узлов-массивов: 1", ((JLabel)named(app, "blockStatus")).getText());
                click(app, "Обойти");
                assertFalse(output(app).isBlank());
                insertSixValues(app);
                assertEquals("Объектов: 7   Узлов-массивов: 4", ((JLabel)named(app, "blockStatus")).getText());
            }
        });
    }

    @Test void wholeInterfaceRendersWithSeparatePanels() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var app = new ListApplicationPanel();
            insertSixValues(app);
            app.setSize(1150, 720);
            layout(app);
            BufferedImage image = new BufferedImage(1150, 720, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            try { app.paint(graphics); } finally { graphics.dispose(); }
            try { javax.imageio.ImageIO.write(image, "png", new File("target/gui-preview.png")); }
            catch (IOException ex) { throw new RuntimeException(ex); }
            assertEquals(3, descendants(app).stream().filter(c -> c instanceof JComboBox<?> && c.getName() != null).count());
        });
    }
    private static void layout(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) if (child instanceof Container nested) layout(nested);
    }
}




