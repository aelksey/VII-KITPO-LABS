package gui

import javax.swing.JButton
import javax.swing.JPanel
import java.awt.FlowLayout
import java.util.function.Consumer

class SerializationPanel(action: Consumer<Boolean>) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    init {
        val save = JButton("Сохранить файл")
        val load = JButton("Открыть файл")
        
        save.addActionListener { action.accept(true) }
        load.addActionListener { action.accept(false) }
        
        add(save)
        add(load)
    }
}
