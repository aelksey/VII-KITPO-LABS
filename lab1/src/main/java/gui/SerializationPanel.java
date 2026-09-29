package gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SerializationPanel extends JPanel {
    public SerializationPanel(Consumer<Boolean> action) {
        super(new FlowLayout(FlowLayout.LEFT));
        JButton save = new JButton("Сохранить файл");
        JButton load = new JButton("Открыть файл");
        save.addActionListener(e -> action.accept(true)); load.addActionListener(e -> action.accept(false));
        add(save); add(load);
    }
}
