package gui

import factory.UserFactory
import javax.swing._
import java.awt._

class ListApplicationPanel extends JPanel(new BorderLayout()) {
  
  private val selection: SelectionPanel = new SelectionPanel()
  private val content: JPanel = new JPanel(new BorderLayout())

  {
    add(selection, BorderLayout.NORTH)
    add(content, BorderLayout.CENTER)
    
    selection.onTypeChanged(new Runnable() {
      override def run(): Unit = {
        selectType()
      }
    })
    
    selection.onTraversalChanged(new Runnable() {
      override def run(): Unit = {
        if (content.getComponentCount() > 0) {
          content.getComponent(0).asInstanceOf[ListEditorPanel[_]].refreshTraversal()
        }
      }
    })
    
    selectType()
  }

  private def selectType(): Unit = {
    content.removeAll()
    content.add(new ListEditorPanel(UserFactory.getBuilderByName(selection.typeName()), selection))
    content.revalidate()
    content.repaint()
  }
}
