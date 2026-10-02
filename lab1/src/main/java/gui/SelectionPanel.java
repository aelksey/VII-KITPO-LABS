package gui;

import factory.SerializeFactory;
import factory.TraverseFactory;
import factory.UserFactory;
import inface.SerializeStrategyInterface;
import inface.TraverseStrategyInterface;
import javax.swing.*;
import java.awt.*;

// Все три общих селектора расположены в верхней панели
public class SelectionPanel extends JPanel {
    private final JComboBox<String> types = new JComboBox<>(UserFactory.getTypeNameList().toArray(String[]::new));
    private final JComboBox<String> traversals = new JComboBox<>(TraverseFactory.getStrategyNameList().toArray(String[]::new));
    private final JComboBox<String> formats = new JComboBox<>(SerializeFactory.getFormatNameList().toArray(String[]::new));

    public SelectionPanel() {
        super(new GridLayout(0, 1));
        JPanel selectors = new JPanel(new FlowLayout(FlowLayout.LEFT));
        types.setName("dataType"); 
        traversals.setName("traversal"); 
        formats.setName("serialization");
        
        selectors.add(new JLabel("Тип данных:")); selectors.add(types);
        selectors.add(new JLabel("Обход итератора:")); selectors.add(traversals);
        selectors.add(new JLabel("Сериализация:")); selectors.add(formats);
        add(selectors);
    }

    public String typeName() { 
        return (String) types.getSelectedItem(); 
    }

    public <T> TraverseStrategyInterface<T> traversal() {
        return TraverseFactory.getStrategyByName((String) traversals.getSelectedItem());
    }

    public SerializeStrategyInterface serializer() {
        return SerializeFactory.getStrategyByName((String) formats.getSelectedItem());
    }

    public void onTypeChanged(Runnable callback) { 
        types.addActionListener(e -> callback.run()); 
    }

    public void onTraversalChanged(Runnable callback) { 
        traversals.addActionListener(e -> callback.run()); 
    }
}
