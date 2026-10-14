package gui;

import javax.swing.*;
import java.awt.*;

//Размер массивов в узлах, без привязки к конкретному типу объектов
public class BlockSettingsPanel extends JPanel {
    private final JSpinner capacity;
    private final JLabel status = new JLabel();

    public BlockSettingsPanel(int initialCapacity, Runnable apply) {
        super(new FlowLayout(FlowLayout.LEFT));
        capacity = new JSpinner(new SpinnerNumberModel(initialCapacity, 1, 16, 1));
        capacity.setName("blockCapacity");
        status.setName("blockStatus");
        add(new JLabel("Ячеек в массиве узла:")); add(capacity);
        JButton button = new JButton("Перегруппировать");
        button.addActionListener(e -> apply.run()); add(button); add(status);
    }
    public int capacity() throws java.text.ParseException {
        capacity.commitEdit(); return (int) capacity.getValue();
    }
    public void updateStatus(int objects, int nodes) {
        status.setText("Объектов: " + objects + "   Узлов-массивов: " + nodes);
    }
}
