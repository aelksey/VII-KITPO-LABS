package gui

import javax.swing.*
import java.awt.Component
import java.awt.FlowLayout
import java.awt.GridLayout
import java.util.function.Consumer

// Ввод значения и операции по логическому номеру
class ListOperationsPanel(action: Consumer<Operation>) : JPanel(GridLayout(0, 1)) {
    
    enum class Operation { APPEND, PREPEND, INSERT, GET, REMOVE, CLEAR }
    
    private val valueField = JTextField(10)
    private val indexSpinner = JSpinner(SpinnerNumberModel(0, 0, 100000, 1))

    init {
        val input = JPanel(FlowLayout(FlowLayout.LEFT))
        valueField.name = "value"
        indexSpinner.name = "logicalIndex"
        
        input.add(JLabel("Значение:") as Component)
        input.add(valueField as Component)
        input.add(JLabel("Логический номер:") as Component)
        input.add(indexSpinner as Component)
        add(input as Component)
        
        val buttons = JPanel(FlowLayout(FlowLayout.LEFT))
        addButton(buttons, "Вставить в конец", Operation.APPEND, action)
        addButton(buttons, "Вставить в начало", Operation.PREPEND, action)
        addButton(buttons, "Вставить по номеру", Operation.INSERT, action)
        addButton(buttons, "Получить", Operation.GET, action)
        addButton(buttons, "Удалить", Operation.REMOVE, action)
        addButton(buttons, "Очистить", Operation.CLEAR, action)
        add(buttons as Component)
    }

    private fun addButton(panel: JPanel, label: String, operation: Operation, action: Consumer<Operation>) {
        val button = JButton(label)
        button.addActionListener { action.accept(operation) }
        panel.add(button as Component)
    }

    fun value(): String = valueField.text

    fun setValue(text: String) {
        valueField.text = text
    }

    @Throws(java.text.ParseException::class)
    fun index(): Int {
        indexSpinner.commitEdit()
        return indexSpinner.value as Int
    }
}
