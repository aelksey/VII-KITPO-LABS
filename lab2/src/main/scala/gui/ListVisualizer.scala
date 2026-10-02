package gui

import core.CustomList
import inface.UserTypeInterface
import inface.TraverseStrategyInterface
import traverse.LinearTraverseStrategy
import javax.swing._
import java.awt._

class ListVisualizer[T](
    private var customList: CustomList[T], 
    private val `type`: UserTypeInterface[T],
    private var iteratorController: ListIteratorController[T] = null // Добавили параметр (опциональный, чтобы не сломать тесты)
) extends JPanel {
  
  private var traversal: TraverseStrategyInterface[T] = new LinearTraverseStrategy[T]()
  private val STEP: Int = 450
  private val ROW: Int = 62
  private val OBJECT_WIDTH: Int = 285

  {
    setBackground(Color.WHITE)
  }

  // Новый метод для связывания контроллера, если он не был передан в конструктор
  def setIteratorController(controller: ListIteratorController[T]): Unit = {
    this.iteratorController = controller
    repaint()
  }

  def setTraversal(traversal: TraverseStrategyInterface[T]): Unit = {
    this.traversal = java.util.Objects.requireNonNull(traversal)
    repaint()
  }

  def setList(newList: CustomList[T]): Unit = {
    this.customList = newList
    revalidate()
    repaint()
  }

  override def getPreferredSize(): Dimension = {
    new Dimension(
      Math.max(700, 40 + (customList.getNodeCount() * STEP)),
      Math.max(230, 100 + (customList.getBlockCapacity() * ROW))
    )
  }

  override protected def paintComponent(graphics: Graphics): Unit = {
    super.paintComponent(graphics)
    val g = graphics.create().asInstanceOf[Graphics2D]
    try {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.setColor(Color.DARK_GRAY)
      
      val blocks = customList.getBlocks()
      var physical = 0
      
      for (b <- 0 until blocks.size()) {
        val x = 20 + (b * STEP)
        val values = blocks.get(b)
        val height = (customList.getBlockCapacity() * ROW) + 15
        
        g.setColor(new Color(225, 237, 250))
        g.fillRoundRect(x, 55, 90, height, 10, 10)
        g.setColor(Color.DARK_GRAY)
        g.drawRoundRect(x, 55, 90, height, 10, 10)
        g.drawString("Узел " + b, x, 23)
        g.drawString("Массив " + values.size() + "/" + customList.getBlockCapacity(), x, 43)
        
        if (b + 1 < blocks.size()) {
          g.drawString("next", x + 175, 18)
          arrow(g, x + 95, 25, x + STEP - 10, 25)
        } else {
          g.drawString("next -> null", x + 130, 25)
        }
        
        for (slot <- 0 until customList.getBlockCapacity()) {
          val y = 65 + (slot * ROW)
          g.setColor(Color.WHITE)
          g.fillRect(x + 10, y, 70, 40)
          g.setColor(Color.GRAY)
          g.drawRect(x + 10, y, 70, 40)
          g.drawString("[" + slot + "]", x + 15, y + 24)
          
          if (slot >= values.size()) {
            g.drawString("", x + 52, y + 24)
          } else {
            val target = x + 130
            g.setColor(new Color(35, 90, 150))
            arrow(g, x + 80, y + 20, target - 5, y + 20)
            
            // ПРОВЕРКА: Находится ли здесь сейчас итератор?
            val isCurrentIterator = iteratorController != null && iteratorController.getCurrentPhysicalIndex == physical

            if (isCurrentIterator) {
              g.setColor(new Color(255, 230, 230)) // Нежно-красный фон для активного элемента
            } else {
              g.setColor(new Color(240, 248, 235)) // Стандартный зеленый фон
            }
            
            g.fillRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8)
            
            if (isCurrentIterator) {
              g.setColor(Color.RED) // Яркая рамка для фокуса
              g.setStroke(new BasicStroke(2.0f))
              g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8)
              g.setStroke(new BasicStroke(1.0f)) // возвращаем как было
              g.setFont(g.getFont.deriveFont(Font.BOLD))
            } else {
              g.setColor(Color.DARK_GRAY)
              g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8)
            }
            
            g.drawString("Объект | лог. номер " + traversal.logicalIndex(physical, customList.getSize()), target + 7, y + 13)
            physical = physical + 1
            
            var label = `type`.toString(values.get(slot))
            while ((label.length() > 1) && (g.getFontMetrics().stringWidth(label) > OBJECT_WIDTH - 15)) {
              label = label.substring(0, label.length() - 2) + "…"
            }
            g.drawString(label, target + 7, y + 33)
            
            // Сбрасываем шрифт обратно на обычный
            g.setFont(g.getFont.deriveFont(Font.PLAIN))
          }
        }
      }
    } finally {
      g.dispose()
    }
  }

  private def arrow(g: Graphics2D, x1: Int, y1: Int, x2: Int, y2: Int): Unit = {
    g.drawLine(x1, y1, x2, y2)
    g.fillPolygon(Array(x2, x2 - 7, x2 - 7), Array(y2, y2 - 4, y2 + 4), 3)
  }
}
