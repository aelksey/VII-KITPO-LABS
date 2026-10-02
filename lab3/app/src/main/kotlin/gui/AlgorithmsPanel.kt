package gui

import factory.SortFactory
import inface.SortStrategyInterface
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import java.awt.FlowLayout
import java.util.function.Consumer

enum class Operation { SORT, TRAVERSE, FIND }

class AlgorithmsPanel(action: Consumer<Operation>) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    
    private val sorts = JComboBox(SortFactory.strategyNameList.toTypedArray())

    init {
        add(sorts)
        addButton("Сортировать", Operation.SORT, action)
        addButton("Обойти", Operation.TRAVERSE, action)
        addButton("Найти", Operation.FIND, action)
    }

    private fun addButton(label: String, operation: Operation, action: Consumer<Operation>) {
        val button = JButton(label)
        button.addActionListener { action.accept(operation) }
        add(button)
    }

    fun sortStrategy(): SortStrategyInterface {
        return SortFactory.getStrategyByName(sorts.selectedItem as String)
    }
}
