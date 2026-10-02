package gui

import scala.swing._

object Main extends SimpleSwingApplication {
  
  override def top: MainFrame = {
    new MainFrame {
      title = "Список с массивом ссылок в каждом элементе"
      peer.setContentPane(new ListApplicationPanel())
      size = new Dimension(1150, 520)
      minimumSize = new Dimension(1000, 600)
      peer.setLocationRelativeTo(null)
    }
  }
}
