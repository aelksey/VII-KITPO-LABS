package gui

import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import java.awt.FlowLayout
import java.text.ParseException

// Параметры генерации нового списка выбранного типа.
class RandomListPanel(generate: Runnable) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    private val maxSizeSpinner = JSpinner(SpinnerNumberModel(10, 1, 1000, 1))

    init {
        maxSizeSpinner.name = "randomMaxSize"
        add(JLabel("Максимум элементов:"))
        add(maxSizeSpinner)
        
        val button = JButton("Случайный список")
        button.toolTipText = "Заменить список новыми случайными значениями; размер - от 1 до указанного максимума"
        button.addActionListener { generate.run() }
        add(button)
    }

    @Throws(ParseException::class)
    fun maxSize(): Int {
        maxSizeSpinner.commitEdit()
        return maxSizeSpinner.value as Int
    }
}
