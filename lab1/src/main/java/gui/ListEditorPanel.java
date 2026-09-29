package gui;

import core.CustomList;
import inface.UserTypeInterface;
import util.TestDataGenerator;
import javax.swing.*;
import java.awt.*;
import java.io.File;

//Связывает независимые панели управления с типизированным списком
public class ListEditorPanel<T> extends JPanel {
    private final CustomList<T> list = new CustomList<>();
    private final UserTypeInterface<T> type;
    private final SelectionPanel selection;
    private final ListVisualizer<T> visualizer;
    private final JTextArea output = new JTextArea(4, 40);
    private final ListOperationsPanel operations;
    private final AlgorithmsPanel algorithms;
    private final BlockSettingsPanel blocks;
    private final RandomListPanel random;

    public ListEditorPanel(UserTypeInterface<T> type, SelectionPanel selection) {
        super(new BorderLayout());
        this.type = type; this.selection = selection;
        visualizer = new ListVisualizer<>(list, type);
        visualizer.setTraversal(selection.traversal());
        output.setEditable(false); output.setName("output");
        operations = new ListOperationsPanel(operation -> execute(() -> operate(operation)));
        algorithms = new AlgorithmsPanel(operation -> execute(() -> algorithm(operation)));
        blocks = new BlockSettingsPanel(list.getBlockCapacity(), () -> execute(this::regroup));
        random = new RandomListPanel(() -> execute(this::generateRandomList));
        blocks.updateStatus(list.getSize(), list.getNodeCount());
        try { operations.setValue(type.serializeValue(type.sampleValues().get(0))); }
        catch (Exception ex) { output.setText(ex.getMessage()); }
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.add(operations); controls.add(algorithms); controls.add(blocks);
        controls.add(random);
        controls.add(new SerializationPanel(save -> execute(() -> fileOperation(save))));
        add(controls, BorderLayout.NORTH);
        add(new JScrollPane(visualizer), BorderLayout.CENTER);
        add(new JScrollPane(output), BorderLayout.SOUTH);
    }

    private T parsedClone() { return type.clone(type.parseValue(operations.value())); }

    private void operate(ListOperationsPanel.Operation operation) throws Exception {
        var traversal = selection.<T>traversal();
        switch (operation) {
            case APPEND -> list.add(parsedClone(), traversal);
            case PREPEND -> list.insert(0, parsedClone(), traversal);
            case INSERT -> list.insert(operations.index(), parsedClone(), traversal);
            case GET -> { output.setText(type.toString(list.get(operations.index(), traversal))); return; }
            case REMOVE -> list.remove(operations.index(), traversal);
            case CLEAR -> list.clear();
        }
        output.setText("Элементов: " + list.getSize());
    }

    private void algorithm(AlgorithmsPanel.Operation operation) {
        switch (operation) {
            case SORT -> {
                list.sort(algorithms.sortStrategy(), type.getTypeComparator());
                output.setText("Список отсортирован. Элементов: " + list.getSize());
            }
            case TRAVERSE -> {
                StringBuilder text = new StringBuilder();
                selection.<T>traversal().traverse(list, v -> text.append(type.toString(v)).append('\n'));
                output.setText(text.length() == 0 ? "Список пуст" : text.toString());
            }
            case FIND -> {
                T sought = parsedClone();
                var traversal = selection.<T>traversal();
                int index = list.firstIndexThat(v -> type.getTypeComparator().compare(v, sought) == 0, traversal);
                output.setText(index == -1 ? "Не найдено" : "Логический номер: " + index
                        + "\nЗначение: " + type.toString(list.get(index, traversal)));
            }
        }
    }

    private void generateRandomList() throws Exception {
        var generated = TestDataGenerator.generateRandomList(type, random.maxSize(), list.getBlockCapacity(), false);
        list.clear();
        generated.forEach(list::add);
        output.setText("Создан случайный список. Элементов: " + list.getSize());
    }

    private void regroup() throws Exception {
        list.setBlockCapacity(blocks.capacity());
        output.setText("Ссылки перегруппированы по " + list.getBlockCapacity() + " ячейки в узле. Объекты сохранены.");
    }

    public void refreshTraversal() {
        visualizer.setTraversal(selection.traversal());
        output.setText("Порядок обхода изменён; логические номера обновлены.");
    }

    private void fileOperation(boolean save) throws Exception {
        var strategy = selection.serializer();
        File file = SerializationFileDialog.choose(this, strategy, save);
        if (file == null) return;
        if (save) strategy.save(file.getAbsolutePath(), list, type);
        else strategy.load(file.getAbsolutePath(), list, type);
        output.setText((save ? "Сохранено: " : "Загружено: ") + file.getAbsolutePath() + "\nЭлементов: " + list.getSize());
    }

    @FunctionalInterface private interface Action { void run() throws Exception; }
    private void execute(Action action) {
        try {
            action.run();
            blocks.updateStatus(list.getSize(), list.getNodeCount());
            visualizer.revalidate(); visualizer.repaint();
        }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE); }
    }
}
