package gui

import factory.SortFactory
import inface.SortStrategyInterface
import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.util.function.Consumer

object AlgorithmsPanel {
  class Operation extends java.lang.Enum[Operation](java.lang.String.valueOf(""), 0)
  
  val SORT: Operation = new Operation()
  val TRAVERSE: Operation = new Operation()
  val FIND: Operation = new Operation()
}

class AlgorithmsPanel(private val action: Consumer[AlgorithmsPanel.Operation]) extends JPanel(new FlowLayout(FlowLayout.LEFT)) {
  
  private val stringArray: Array[String] = SortFactory.getStrategyNameList().toArray(new Array[String](0))
  private val sorts: JComboBox[String] = new JComboBox[String](stringArray)

  {
    add(sorts)
    button("Сортировать", AlgorithmsPanel.SORT, action)
    button("Обойти", AlgorithmsPanel.TRAVERSE, action)
    button("Найти", AlgorithmsPanel.FIND, action)
  }

  private def button(label: String, operation: AlgorithmsPanel.Operation, action: Consumer[AlgorithmsPanel.Operation]): Unit = {
    val button = new JButton(label)
    button.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        action.accept(operation)
      }
    })
    add(button)
  }

  def sortStrategy(): SortStrategyInterface = {
    SortFactory.getStrategyByName(sorts.getSelectedItem().asInstanceOf[String])
  }
}
