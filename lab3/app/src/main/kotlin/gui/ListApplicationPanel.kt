package gui

import factory.UserFactory
import inface.UserTypeInterface
import javax.swing.JPanel
import java.awt.BorderLayout

// Собирает верхние селекторы, редактор выбранного типа и элементы управления итератором
class ListApplicationPanel : JPanel(BorderLayout()) {
    
    private val selection = SelectionPanel()
    private val content = JPanel(BorderLayout())
    private val bottomPanel = JPanel(BorderLayout())

    init {
        add(selection, BorderLayout.NORTH)
        add(content, BorderLayout.CENTER)
        add(bottomPanel, BorderLayout.SOUTH)

        selection.onTypeChanged { selectType() }
        
        selection.onTraversalChanged {
            if (content.componentCount > 0) {
                // Извлекаем текущий редактор и сбрасываем его контроллер итератора
                val editor = content.getComponent(0) as ListEditorPanel<*>
                editor.visualizer.iteratorController?.reset()
                editor.refreshTraversal()
                repaint()
            }
        }

        selectType()
    }

    private fun selectType() {
        val userType = UserFactory.getBuilderByName(selection.typeName())
        initTypedComponents(userType)
    }

    // Вспомогательный generic-метод фиксирует единый тип E для всей цепочки вызовов
    @Suppress("UNCHECKED_CAST")
    private fun <E> initTypedComponents(userType: UserTypeInterface<E>) {
        content.removeAll()
        bottomPanel.removeAll()

        // 1. Создаем строго типизированный редактор
        val typedEditor = ListEditorPanel(userType, selection)
        content.add(typedEditor, BorderLayout.CENTER)

        // 2. Создаем контроллер, строго привязанный к типу E списка
        val iteratorController = ListIteratorController(typedEditor.list, selection)

        // 3. Теперь типы совпадают без варнингов и ошибок компиляции
        typedEditor.visualizer.iteratorController = iteratorController

        // 4. Инициализируем панель кнопок
        val iteratorPanel = IteratorOperationsPanel { op ->
            when (op) {
                ListIteratorController.Op.TO_BEGIN -> iteratorController.toBegin()
                ListIteratorController.Op.TO_END -> iteratorController.toEnd()
                ListIteratorController.Op.NEXT -> iteratorController.next()
                ListIteratorController.Op.PREV -> iteratorController.prev()
                null -> {}
            }
            typedEditor.visualizer.repaint()
        }

        bottomPanel.add(iteratorPanel, BorderLayout.CENTER)

        content.revalidate()
        content.repaint()
        bottomPanel.revalidate()
        bottomPanel.repaint()
    }
}
