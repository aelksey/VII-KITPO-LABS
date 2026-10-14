package gui

import core.CustomList
import inface.UserTypeInterface
import inface.TraverseStrategyInterface
import traverse.LinearTraverseStrategy
import javax.swing.JPanel
import java.awt.*

class ListVisualizer<T>(
    private var customList: CustomList<T>,
    private val type: UserTypeInterface<T>
) : JPanel() {

    var iteratorController: ListIteratorController<T>? = null
        set(value) {
            field = value
            repaint()
        }

    private var traversal: TraverseStrategyInterface<T> = LinearTraverseStrategy()
    
    companion object {
        private const val STEP = 450
        private const val ROW = 62
        private const val OBJECT_WIDTH = 285
    }

    init {
        background = Color.WHITE
    }

    fun setTraversal(traversal: TraverseStrategyInterface<T>) {
        this.traversal = java.util.Objects.requireNonNull(traversal)
        repaint()
    }

    fun setList(newList: CustomList<T>) {
        this.customList = newList
        revalidate()
        repaint()
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(
            Math.max(700, 40 + (customList.nodeCount * STEP)),
            Math.max(230, 100 + (customList.blockCapacity * ROW))
        )
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        val g = graphics.create() as Graphics2D
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            
            val blocks = customList.getBlocks()
            var physical = 0
            
            for (b in blocks.indices) {
                val x = 20 + (b * STEP)
                val values = blocks[b]
                val height = (customList.blockCapacity * ROW) + 15
                
                g.color = Color(225, 237, 250)
                g.fillRoundRect(x, 55, 90, height, 10, 10)
                g.color = Color.DARK_GRAY
                g.drawRoundRect(x, 55, 90, height, 10, 10)
                g.drawString("Узел $b", x, 23)
                g.drawString("Массив ${values.size}/${customList.blockCapacity}", x, 43)
                
                if (b + 1 < blocks.size) {
                    g.drawString("next", x + 175, 18)
                    arrow(g, x + 95, 25, x + STEP - 10, 25)
                } else {
                    g.drawString("next -> null", x + 130, 25)
                }
                
                for (slot in 0 until customList.blockCapacity) {
                    val y = 65 + (slot * ROW)
                    g.color = Color.WHITE
                    g.fillRect(x + 10, y, 70, 40)
                    g.color = Color.GRAY
                    g.drawRect(x + 10, y, 70, 40)
                    g.drawString("[$slot]", x + 15, y + 24)
                    
                    if (slot >= values.size) {
                        g.drawString("", x + 52, y + 24)
                        continue
                    }
                    
                    val target = x + 130
                    g.color = Color(35, 90, 150)
                    arrow(g, x + 80, y + 20, target - 5, y + 20)
                    
                    // ПРОВЕРКА: Находится ли здесь сейчас итератор?
                    val isCurrentIterator = iteratorController != null && iteratorController!!.currentPhysicalIndex == physical

                    if (isCurrentIterator) {
                        g.color = Color(255, 230, 230) // Нежно-красный фон
                    } else {
                        g.color = Color(240, 248, 235) // Стандартный зеленый фон
                    }
                    
                    g.fillRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8)
                    
                    if (isCurrentIterator) {
                        g.color = Color.RED // Яркая рамка фокуса
                        g.stroke = BasicStroke(2.0f)
                        g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8)
                        g.stroke = BasicStroke(1.0f)
                        g.font = g.font.deriveFont(Font.BOLD)
                    } else {
                        g.color = Color.DARK_GRAY
                        g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8)
                    }
                    
                    g.drawString("Объект | лог. номер ${traversal.logicalIndex(physical, customList.size)}", target + 7, y + 13)
                    physical++
                    
                    var label = type.toString(values[slot])
                    while (label.length > 1 && g.fontMetrics.stringWidth(label) > OBJECT_WIDTH - 15) {
                        label = label.substring(0, label.length - 2) + "…"
                    }
                    g.drawString(label, target + 7, y + 33)
                    
                    g.font = g.font.deriveFont(Font.PLAIN) // Сброс шрифта
                }
            }
        } finally {
            g.dispose()
        }
    }

    private fun arrow(g: Graphics2D, x1: Int, y1: Int, x2: Int, y2: Int) {
        g.drawLine(x1, y1, x2, y2)
        g.fillPolygon(intArrayOf(x2, x2 - 7, x2 - 7), intArrayOf(y2, y2 - 4, y2 + 4), 3)
    }
}
