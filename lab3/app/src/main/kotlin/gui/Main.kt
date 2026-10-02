package gui

import javax.swing.JFrame
import javax.swing.SwingUtilities
import java.awt.Dimension

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val frame = JFrame("Список с массивом ссылок в каждом элементе")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane = ListApplicationPanel()
        frame.setSize(1150, 520)
        frame.minimumSize = Dimension(1000, 600)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
}
