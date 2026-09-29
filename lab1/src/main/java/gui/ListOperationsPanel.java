package gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

//Ввод значения и операции по логическому номеру
public class ListOperationsPanel extends JPanel {
    public enum Operation { APPEND, PREPEND, INSERT, GET, REMOVE, CLEAR }
    private final JTextField value = new JTextField(10);
    private final JSpinner index = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));

    public ListOperationsPanel(Consumer<Operation> action) {
        super(new GridLayout(0, 1));
        JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT));
        value.setName("value"); index.setName("logicalIndex");
        input.add(new JLabel("Значение:")); input.add(value);
        input.add(new JLabel("Логический номер:")); input.add(index);
        add(input);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        button(buttons, "Вставить в конец", Operation.APPEND, action);
        button(buttons, "Вставить в начало", Operation.PREPEND, action);
        button(buttons, "Вставить по номеру", Operation.INSERT, action);
        button(buttons, "Получить", Operation.GET, action);
        button(buttons, "Удалить", Operation.REMOVE, action);
        button(buttons, "Очистить", Operation.CLEAR, action);
        add(buttons);
    }
    private void button(JPanel panel, String label, Operation operation, Consumer<Operation> action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.accept(operation)); panel.add(button);
    }
    public String value() { return value.getText(); }
    public void setValue(String text) { value.setText(text); }
    public int index() throws java.text.ParseException { index.commitEdit(); return (int) index.getValue(); }
}
