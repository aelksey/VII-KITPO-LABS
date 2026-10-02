package gui;

import factory.UserFactory;
import inface.UserTypeInterface;
import javax.swing.*;
import java.awt.*;

// Собирает верхние селекторы, редактор выбранного типа и элементы управления итератором
public class ListApplicationPanel extends JPanel {
    private final SelectionPanel selection = new SelectionPanel();
    private final JPanel content = new JPanel(new BorderLayout());
    private final JPanel bottomPanel = new JPanel(new BorderLayout());

    public ListApplicationPanel() {
        super(new BorderLayout());
        
        add(selection, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Используем лямбду со вспомогательным вызовом обобщенного метода
        selection.onTypeChanged(this::selectType);
        
        selection.onTraversalChanged(() -> {
            if (content.getComponentCount() > 0) {
                // Извлекаем текущий редактор и сбрасываем его контроллер итератора
                ListEditorPanel<?> editor = (ListEditorPanel<?>) content.getComponent(0);
                if (editor.getVisualizer() != null) {
                    var controller = editor.getVisualizer().getIteratorController();
                    if (controller != null) {
                        controller.reset();
                    }
                }
                editor.refreshTraversal();
                repaint();
            }
        });

        selectType();
    }

    private void selectType() {
        // Вызываем вспомогательный типизированный метод, чтобы избежать capture-конфликтов
        var userType = UserFactory.getBuilderByName(selection.typeName());
        initTypedComponents(userType);
    }

    // Вспомогательный generic-метод фиксирует единый тип E для всей цепочки вызовов
    @SuppressWarnings("unchecked")
    private <E> void initTypedComponents(UserTypeInterface<E> userType) {
        content.removeAll();
        bottomPanel.removeAll();

        // 1. Создаем строго типизированный редактор
        ListEditorPanel<E> typedEditor = new ListEditorPanel<>(userType, selection);
        content.add(typedEditor, BorderLayout.CENTER);

        // 2. Создаем контроллер, строго привязанный к типу E списка
        ListIteratorController<E> iteratorController = new ListIteratorController<>(typedEditor.getList(), selection);

        // 3. Теперь типы совпадают без варнингов и ошибок компиляции
        typedEditor.getVisualizer().setIteratorController(iteratorController);

        // 4. Инициализируем панель кнопок
        IteratorOperationsPanel iteratorPanel = new IteratorOperationsPanel(op -> {
            switch (op) {
                case TO_BEGIN -> iteratorController.toBegin();
                case TO_END -> iteratorController.toEnd();
                case NEXT -> iteratorController.next();
                case PREV -> iteratorController.prev();
            }
            typedEditor.getVisualizer().repaint();
        });

        bottomPanel.add(iteratorPanel, BorderLayout.CENTER);

        content.revalidate();
        content.repaint();
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }
}
