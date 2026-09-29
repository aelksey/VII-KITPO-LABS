package gui;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

// Параметры генерации нового списка выбранного типа.
public class RandomListPanel extends JPanel {
    private final JSpinner maxSize = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));

    public RandomListPanel(Runnable generate) {
        super(new FlowLayout(FlowLayout.LEFT));
        maxSize.setName("randomMaxSize");
        add(new JLabel("Максимум элементов:"));
        add(maxSize);
        JButton button = new JButton("Случайный список");
        button.setToolTipText("Заменить список новыми случайными значениями; размер - от 1 до указанного максимума");
        button.addActionListener(e -> generate.run());
        add(button);
    }

    public int maxSize() throws ParseException {
        maxSize.commitEdit();
        return (int) maxSize.getValue();
    }
}
