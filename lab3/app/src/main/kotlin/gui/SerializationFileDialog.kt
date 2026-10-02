package gui

import inface.SerializeStrategyInterface
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Component
import java.io.File
import java.io.IOException
import java.util.Locale

// Единая политика расширений для открытия и сохранения файлов
object SerializationFileDialog {

    fun createChooser(strategy: SerializeStrategyInterface): JFileChooser {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.isMultiSelectionEnabled = false
        chooser.isAcceptAllFileFilterUsed = false
        val extension = strategy.fileExtension
        chooser.fileFilter = FileNameExtensionFilter("\${strategy.formatName} (*.\$extension)", extension)
        return chooser
    }

    @Throws(IOException::class)
    fun resolveSelection(file: File, strategy: SerializeStrategyInterface, save: Boolean): File {
        var resultFile = file
        val extension = strategy.fileExtension
        if (save && !resultFile.name.contains(".")) {
            resultFile = File(resultFile.parentFile, resultFile.name + "." + extension)
        }
        if (!resultFile.name.lowercase(Locale.ROOT).endsWith("." + extension.lowercase(Locale.ROOT))) {
            throw IOException("Для выбранного формата требуется расширение .\$extension")
        }
        if (resultFile.isDirectory) throw IOException("Выберите файл, а не каталог")
        if (!save && !resultFile.isFile) throw IOException("Выбранный файл не существует")
        return resultFile
    }

    @Throws(IOException::class)
    fun choose(parent: Component, strategy: SerializeStrategyInterface, save: Boolean): File? {
        val chooser = createChooser(strategy)
        chooser.dialogTitle = (if (save) "Сохранить: " else "Открыть: ") + strategy.formatName
        if (save) {
            chooser.selectedFile = File("list." + strategy.fileExtension)
        }
        
        val result = if (save) chooser.showSaveDialog(parent) else chooser.showOpenDialog(parent)
        if (result != JFileChooser.APPROVE_OPTION) return null
        
        // Фильтр не запрещает ввод имени вручную, поэтому расширение проверяем повторно.
        val file = resolveSelection(chooser.selectedFile, strategy, save)
        if (save && file.exists() && JOptionPane.showConfirmDialog(
                parent,
                "Заменить файл «\${file.name}»?",
                "Сохранение",
                JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION
        ) {
            return null
        }
        return file
    }
}
