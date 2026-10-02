package gui

import javax.swing._
import java.awt._
import java.util.function.Consumer

class IteratorOperationsPanel(private val callback: Consumer[ListIteratorController.Op]) extends JPanel(new FlowLayout(FlowLayout.LEFT)) {
  
  {
    add(new JLabel("Итератор:"))
    
    val btnBegin = new JButton("|< В начало")
    btnBegin.addActionListener(_ => callback.accept(ListIteratorController.TO_BEGIN))
    add(btnBegin)
    
    val btnPrev = new JButton("< Шаг назад")
    btnPrev.addActionListener(_ => callback.accept(ListIteratorController.PREV))
    add(btnPrev)
    
    val btnNext = new JButton("Шаг вперед >")
    btnNext.addActionListener(_ => callback.accept(ListIteratorController.NEXT))
    add(btnNext)
    
    val btnEnd = new JButton("В конец >|")
    btnEnd.addActionListener(_ => callback.accept(ListIteratorController.TO_END))
    add(btnEnd)
  }
}
