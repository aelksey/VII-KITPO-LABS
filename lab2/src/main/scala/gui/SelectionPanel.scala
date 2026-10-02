package gui

import factory.SerializeFactory
import factory.TraverseFactory
import factory.UserFactory
import inface.SerializeStrategyInterface
import inface.TraverseStrategyInterface
import javax.swing._
import java.awt._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

class SelectionPanel extends JPanel(new GridLayout(0, 1)) {
  
  private val types: JComboBox[String] = new JComboBox[String](UserFactory.getTypeNameList().toArray(new Array[String](0)))
  private val traversals: JComboBox[String] = new JComboBox[String](TraverseFactory.getStrategyNameList().toArray(new Array[String](0)))
  private val formats: JComboBox[String] = new JComboBox[String](SerializeFactory.getFormatNameList().toArray(new Array[String](0)))

  {
    val selectors = new JPanel(new FlowLayout(FlowLayout.LEFT))
    types.setName("dataType")
    traversals.setName("traversal")
    formats.setName("serialization")
    
    selectors.add(new JLabel("Тип данных:"))
    selectors.add(types)
    selectors.add(new JLabel("Обход итератора:"))
    selectors.add(traversals)
    selectors.add(new JLabel("Сериализация:"))
    selectors.add(formats)
    add(selectors)
  }

  def typeName(): String = {
    types.getSelectedItem().asInstanceOf[String]
  }

  def traversal[T](): TraverseStrategyInterface[T] = {
    TraverseFactory.getStrategyByName(traversals.getSelectedItem().asInstanceOf[String])
  }

  def serializer(): SerializeStrategyInterface = {
    SerializeFactory.getStrategyByName(formats.getSelectedItem().asInstanceOf[String])
  }

  def onTypeChanged(callback: Runnable): Unit = {
    types.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        callback.run()
      }
    })
  }

  def onTraversalChanged(callback: Runnable): Unit = {
    traversals.addActionListener(new ActionListener() {
      override def actionPerformed(e: ActionEvent): Unit = {
        callback.run()
      }
    })
  }
}
