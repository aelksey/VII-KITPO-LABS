package gui

import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

class RandomListPanel(private val generate: Runnable) extends JPanel(new FlowLayout(FlowLayout.LEFT)) {
  
  private val maxSizeSpinner: JSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1))

  {
    maxSizeSpinner.setName("randomMaxSize")
    add(new JLabel("Максимум элементов:"))
    add(maxSizeSpinner)
    
    val button = new JButton("Случайный список")
    button.setToolTipText("Заменить список новыми случайными значениями; размер - от 1 до указанного максимума")
    button.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        generate.run()
      }
    })
    add(button)
  }

  @throws[java.text.ParseException]
  def maxSize(): Int = {
    maxSizeSpinner.commitEdit()
    maxSizeSpinner.getValue().asInstanceOf[java.lang.Integer].intValue()
  }
}
