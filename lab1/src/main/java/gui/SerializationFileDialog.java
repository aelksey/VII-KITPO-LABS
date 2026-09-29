package gui;

import inface.SerializeStrategyInterface;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

// Единая политика расширений для открытия и сохранения файлов
public final class SerializationFileDialog {
    private SerializationFileDialog() { }

    public static JFileChooser createChooser(SerializeStrategyInterface strategy) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        chooser.setAcceptAllFileFilterUsed(false);
        String extension = strategy.fileExtension();
        chooser.setFileFilter(new FileNameExtensionFilter(strategy.formatName() + " (*." + extension + ")", extension));
        return chooser;
    }

    public static File resolveSelection(File file, SerializeStrategyInterface strategy, boolean save) throws IOException {
        String extension = strategy.fileExtension();
        if (save && !file.getName().contains(".")) file = new File(file.getParentFile(), file.getName() + "." + extension);
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith("." + extension.toLowerCase(Locale.ROOT)))
            throw new IOException("Для выбранного формата требуется расширение ." + extension);
        if (file.isDirectory()) throw new IOException("Выберите файл, а не каталог");
        if (!save && !file.isFile()) throw new IOException("Выбранный файл не существует");
        return file;
    }

    public static File choose(Component parent, SerializeStrategyInterface strategy, boolean save) throws IOException {
        JFileChooser chooser = createChooser(strategy);
        chooser.setDialogTitle((save ? "Сохранить: " : "Открыть: ") + strategy.formatName());
        if (save) chooser.setSelectedFile(new File("list." + strategy.fileExtension()));
        int result = save ? chooser.showSaveDialog(parent) : chooser.showOpenDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return null;
        // Фильтр не запрещает ввод имени вручную, поэтому расширение проверяем повторно.
        File file = resolveSelection(chooser.getSelectedFile(), strategy, save);
        if (save && file.exists() && JOptionPane.showConfirmDialog(parent,
                "Заменить файл «" + file.getName() + "»?", "Сохранение", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return null;
        return file;
    }
}
