package gui

import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

class BlockSettingsPanel(private val initialCapacity: Int, private val applyRunnable: Runnable) extends JPanel(new FlowLayout(FlowLayout.LEFT)) {
  
  // Переименовали поле во избежание конфликта с методом геттера
  private val capacitySpinner: JSpinner = new JSpinner(new SpinnerNumberModel(initialCapacity, 1, 16, 1))
  private val status: JLabel = new JLabel()

  {
    capacitySpinner.setName("blockCapacity")
    status.setName("blockStatus")
    add(new JLabel("Ячеек в массиве узла:"))
    add(capacitySpinner)
    
    val button = new JButton("Перегруппировать")
    button.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        applyRunnable.run()
      }
    })
    add(button)
    add(status)
  }

  @throws[java.text.ParseException]
  def capacity(): Int = {
    capacitySpinner.commitEdit()
    capacitySpinner.getValue().asInstanceOf[java.lang.Integer].intValue()
  }

  def updateStatus(objects: Int, nodes: Int): Unit = {
    status.setText("Объектов: " + objects + "   Узлов-массивов: " + nodes)
  }
}
