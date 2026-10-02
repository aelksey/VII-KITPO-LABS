package gui

import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.util.function.Consumer

class SerializationPanel(private val action: Consumer[java.lang.Boolean]) extends JPanel(new FlowLayout(FlowLayout.LEFT)) {
  
  {
    val save: JButton = new JButton("Сохранить файл")
    val load: JButton = new JButton("Открыть файл")
    
    save.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        action.accept(java.lang.Boolean.TRUE)
      }
    })
    
    load.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        action.accept(java.lang.Boolean.FALSE)
      }
    })
    
    add(save)
    add(load)
  }
}
