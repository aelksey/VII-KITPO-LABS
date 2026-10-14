package gui

import factory.SerializeFactory
import factory.TraverseFactory
import factory.UserFactory
import inface.SerializeStrategyInterface
import inface.TraverseStrategyInterface
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.FlowLayout
import java.awt.GridLayout

// Все три общих селектора расположены в верхней панели
class SelectionPanel : JPanel(GridLayout(0, 1)) {
    private val types = JComboBox(UserFactory.typeNameList.toTypedArray())
    private val traversals = JComboBox(TraverseFactory.strategyNameList.toTypedArray())
    private val formats = JComboBox(SerializeFactory.formatNameList.toTypedArray())

    init {
        val selectors = JPanel(FlowLayout(FlowLayout.LEFT))
        types.name = "dataType"
        traversals.name = "traversal"
        formats.name = "serialization"
        
        selectors.add(JLabel("Тип данных:"))
        selectors.add(types)
        selectors.add(JLabel("Обход итератора:"))
        selectors.add(traversals)
        selectors.add(JLabel("Сериализация:"))
        selectors.add(formats)
        add(selectors)
    }

    fun typeName(): String {
        return types.selectedItem as String
    }

    fun <T> traversal(): TraverseStrategyInterface<T> {
        return TraverseFactory.getStrategyByName(traversals.selectedItem as String)
            ?: throw IllegalStateException("Стратегия обхода не найдена")
    }

    fun serializer(): SerializeStrategyInterface {
        return SerializeFactory.getStrategyByName(formats.selectedItem as String)
    }

    fun onTypeChanged(callback: Runnable) {
        types.addActionListener { callback.run() }
    }

    fun onTraversalChanged(callback: Runnable) {
        traversals.addActionListener { callback.run() }
    }
}
