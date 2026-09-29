package gui;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Список с массивом ссылок в каждом элементе");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ListApplicationPanel());
            frame.setSize(1150, 520);
            frame.setMinimumSize(new java.awt.Dimension(1000, 600));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
