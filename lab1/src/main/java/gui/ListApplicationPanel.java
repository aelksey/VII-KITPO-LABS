package gui;

import factory.UserFactory;
import javax.swing.*;
import java.awt.*;

//Собирает верхние селекторы и редактор выбранного типа
public class ListApplicationPanel extends JPanel {
    private final SelectionPanel selection = new SelectionPanel();
    private final JPanel content = new JPanel(new BorderLayout());

    public ListApplicationPanel() {
        super(new BorderLayout());
        add(selection, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        selection.onTypeChanged(this::selectType);
        selection.onTraversalChanged(() -> {
            if (content.getComponentCount() > 0)
                ((ListEditorPanel<?>) content.getComponent(0)).refreshTraversal();
        });
        selectType();
    }

    private void selectType() {
        content.removeAll();
        content.add(new ListEditorPanel<>(UserFactory.getBuilderByName(selection.typeName()), selection));
        content.revalidate();
        content.repaint();
    }
}
