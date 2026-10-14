package gui

import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import java.awt.FlowLayout

// Размер массивов в узлах, без привязки к конкретному типу объектов
class BlockSettingsPanel(initialCapacity: Int, apply: Runnable) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    
    private val capacity: JSpinner = JSpinner(SpinnerNumberModel(initialCapacity, 1, 16, 1))
    private val status = JLabel()

    init {
        capacity.name = "blockCapacity"
        status.name = "blockStatus"
        add(JLabel("Ячеек в массиве узла:"))
        add(capacity)
        
        val button = JButton("Перегруппировать")
        button.addActionListener { apply.run() }
        add(button)
        add(status)
    }

    @Throws(java.text.ParseException::class)
    fun capacity(): Int {
        capacity.commitEdit()
        return capacity.value as Int
    }

    fun updateStatus(objects: Int, nodes: Int) {
        status.text = "Объектов: $objects   Узлов-массивов: $nodes"
    }
}
