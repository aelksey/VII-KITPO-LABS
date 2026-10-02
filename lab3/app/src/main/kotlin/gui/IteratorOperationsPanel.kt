package gui

import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.FlowLayout
import java.util.function.Consumer

class IteratorOperationsPanel(callback: Consumer<ListIteratorController.Op>) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    
    init {
        add(JLabel("Итератор:"))
        
        val btnBegin = JButton("|< В начало")
        btnBegin.addActionListener { callback.accept(ListIteratorController.Op.TO_BEGIN) }
        add(btnBegin)
        
        val btnPrev = JButton("< Шаг назад")
        btnPrev.addActionListener { callback.accept(ListIteratorController.Op.PREV) }
        add(btnPrev)
        
        val btnNext = JButton("Шаг вперед >")
        btnNext.addActionListener { callback.accept(ListIteratorController.Op.NEXT) }
        add(btnNext)
        
        val btnEnd = JButton("В конец >|")
        btnEnd.addActionListener { callback.accept(ListIteratorController.Op.TO_END) }
        add(btnEnd)
    }
}
