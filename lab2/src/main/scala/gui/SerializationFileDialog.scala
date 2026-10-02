package gui

import inface.SerializeStrategyInterface
import javax.swing._
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Component
import java.io.File
import java.io.IOException
import java.util.Locale

object SerializationFileDialog {

  def createChooser(strategy: SerializeStrategyInterface): JFileChooser = {
    val chooser = new JFileChooser()
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
    chooser.setMultiSelectionEnabled(false)
    chooser.setAcceptAllFileFilterUsed(false)
    val extension = strategy.fileExtension()
    chooser.setFileFilter(new FileNameExtensionFilter(strategy.formatName() + " (*." + extension + ")", extension))
    chooser
  }

  @throws[IOException]
  def resolveSelection(file: File, strategy: SerializeStrategyInterface, save: Boolean): File = {
    var resultFile = file
    val extension = strategy.fileExtension()
    if (save && !resultFile.getName().contains(".")) {
      resultFile = new File(resultFile.getParentFile(), resultFile.getName() + "." + extension)
    }
    if (!resultFile.getName().toLowerCase(Locale.ROOT).endsWith("." + extension.toLowerCase(Locale.ROOT))) {
      throw new IOException("Для выбранного формата требуется расширение ." + extension)
    }
    if (resultFile.isDirectory()) {
      throw new IOException("Выберите файл, а не каталог")
    }
    if (!save && !resultFile.isFile()) {
      throw new IOException("Выбранный файл не существует")
    }
    resultFile
  }

  @throws[IOException]
  def choose(parent: Component, strategy: SerializeStrategyInterface, save: Boolean): File = {
    val chooser = createChooser(strategy)
    chooser.setDialogTitle((if (save) { "Сохранить: " } else { "Открыть: " }) + strategy.formatName())
    if (save) {
      chooser.setSelectedFile(new File("list." + strategy.fileExtension()))
    }
    val result = if (save) { chooser.showSaveDialog(parent) } else { chooser.showOpenDialog(parent) }
    if (result != JFileChooser.APPROVE_OPTION) {
      null
    } else {
      val file = resolveSelection(chooser.getSelectedFile(), strategy, save)
      if (save && file.exists() && (JOptionPane.showConfirmDialog(parent, "Заменить файл «" + file.getName() + "»?", "Сохранение", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) {
        null
      } else {
        file
      }
    }
  }
}
