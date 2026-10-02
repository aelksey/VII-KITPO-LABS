package gui

import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.util.function.Consumer

object ListOperationsPanel {
  class Operation extends java.lang.Enum[Operation](java.lang.String.valueOf(""), 0)
  
  val APPEND: Operation = new Operation()
  val PREPEND: Operation = new Operation()
  val INSERT: Operation = new Operation()
  val GET: Operation = new Operation()
  val REMOVE: Operation = new Operation()
  val CLEAR: Operation = new Operation()
}

class ListOperationsPanel(private val action: Consumer[ListOperationsPanel.Operation]) extends JPanel(new GridLayout(0, 1)) {
  
  private val valueField: JTextField = new JTextField(10)
  private val indexSpinner: JSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1))

  {
    val input = new JPanel(new FlowLayout(FlowLayout.LEFT))
    valueField.setName("value")
    indexSpinner.setName("logicalIndex")
    
    input.add(new JLabel("Значение:"))
    input.add(valueField)
    input.add(new JLabel("Логический номер:"))
    input.add(indexSpinner)
    add(input)
    
    val buttons = new JPanel(new FlowLayout(FlowLayout.LEFT))
    button(buttons, "Вставить в конец", ListOperationsPanel.APPEND, action)
    button(buttons, "Вставить в начало", ListOperationsPanel.PREPEND, action)
    button(buttons, "Вставить по номеру", ListOperationsPanel.INSERT, action)
    button(buttons, "Получить", ListOperationsPanel.GET, action)
    button(buttons, "Удалить", ListOperationsPanel.REMOVE, action)
    button(buttons, "Очистить", ListOperationsPanel.CLEAR, action)
    add(buttons)
  }

  private def button(panel: JPanel, label: String, operation: ListOperationsPanel.Operation, action: Consumer[ListOperationsPanel.Operation]): Unit = {
    val button = new JButton(label)
    button.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        action.accept(operation)
      }
    })
    panel.add(button)
  }

  def value(): String = {
    valueField.getText()
  }

  def setValue(text: String): Unit = {
    valueField.setText(text)
  }

  @throws[java.text.ParseException]
  def index(): Int = {
    indexSpinner.commitEdit()
    indexSpinner.getValue().asInstanceOf[java.lang.Integer].intValue()
  }
}
