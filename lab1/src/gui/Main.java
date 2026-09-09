package gui;

import core.CustomList;
import types.StringStrategy;

import javax.swing.*;
import java.awt.*;

public class Main {


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Визуализатор CustomList (Многоуровневый список)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 450);

            // Создаем список строк и наполняем базовыми элементами
            CustomList<String> list = new CustomList<>();
            StringStrategy userType = new StringStrategy();

            list.add("Node_A");
            list.add("Node_B");
            list.add("Node_C");
            list.add("Node_D");
            list.add("Node_E");

            // Строим произвольные многоуровневые связи для демонстрации:
            // Параметры: setLinkAtLevel(индекс_источника, уровень, индекс_цели)
            list.setLinkAtLevel(0, 1, 2); // А (L1) -> C
            list.setLinkAtLevel(0, 2, 4); // А (L2) -> E
            list.setLinkAtLevel(2, 1, 4); // C (L1) -> E
            list.setLinkAtLevel(1, 3, 3); // B (L3) -> D

            // Панель отрисовки
            ListVisualizer<String> visualizer = new ListVisualizer<>(list, userType);
            
            // Панель управления (Кнопки)
            JPanel controlPanel = new JPanel();
            controlPanel.setBackground(new Color(240, 240, 240));

            JTextField inputField = new JTextField("Новый_Узел", 8);
            JButton addButton = new JButton("Добавить в конец");
            JButton clearButton = new JButton("Очистить");
            
            // Поля для создания кастомной связи
            JTextField fromTxt = new JTextField("0", 2);
            JTextField lvlTxt = new JTextField("1", 2);
            JTextField toTxt = new JTextField("3", 2);
            JButton linkButton = new JButton("Связать уровни");

            controlPanel.add(new JLabel("Текст:"));
            controlPanel.add(inputField);
            controlPanel.add(addButton);
            controlPanel.add(clearButton);
            controlPanel.add(new JSeparator(JSeparator.VERTICAL));
            controlPanel.add(new JLabel("Из инд:"));
            controlPanel.add(fromTxt);
            controlPanel.add(new JLabel("Уровень L:"));
            controlPanel.add(lvlTxt);
            controlPanel.add(new JLabel("В инд:"));
            controlPanel.add(toTxt);
            controlPanel.add(linkButton);

            // Логика кнопок
            addButton.addActionListener(e -> {
                String text = inputField.getText().trim();
                if (!text.isEmpty()) {
                    list.add(text);
                    visualizer.repaint();
                }
            });

            clearButton.addActionListener(e -> {
                list.clear();
                visualizer.repaint();
            });

            linkButton.addActionListener(e -> {
                try {
                    int from = Integer.parseInt(fromTxt.getText());
                    int lvl = Integer.parseInt(lvlTxt.getText());
                    int to = Integer.parseInt(toTxt.getText());
                    
                    list.setLinkAtLevel(from, lvl, to);
                    visualizer.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Ошибка ввода индексов: " + ex.getMessage());
                }
            });

            // Размещение в окне
            frame.setLayout(new BorderLayout());
            frame.add(new JScrollPane(visualizer), BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
