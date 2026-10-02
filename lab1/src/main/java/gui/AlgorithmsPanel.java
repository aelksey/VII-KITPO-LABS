package gui;

import factory.SortFactory;
import inface.SortStrategyInterface;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class AlgorithmsPanel extends JPanel {
    public enum Operation { SORT, TRAVERSE, FIND }
    private final JComboBox<String> sorts = new JComboBox<>(SortFactory.getStrategyNameList().toArray(String[]::new));

    public AlgorithmsPanel(Consumer<Operation> action) {
        super(new FlowLayout(FlowLayout.LEFT));
        add(sorts);
        button("Сортировать", Operation.SORT, action);
        button("Обойти", Operation.TRAVERSE, action);
        button("Найти", Operation.FIND, action);
    }

    private void button(String label, Operation operation, Consumer<Operation> action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.accept(operation)); 
        add(button);
    }

    public SortStrategyInterface sortStrategy() { 
        return SortFactory.getStrategyByName((String) sorts.getSelectedItem()); 
    }
}
