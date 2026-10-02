package gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class IteratorOperationsPanel extends JPanel {
    
    public IteratorOperationsPanel(Consumer<ListIteratorController.Op> callback) {
        super(new FlowLayout(FlowLayout.LEFT));
        
        add(new JLabel("Итератор:"));
        
        JButton btnBegin = new JButton("|< В начало");
        btnBegin.addActionListener(e -> callback.accept(ListIteratorController.Op.TO_BEGIN));
        add(btnBegin);
        
        JButton btnPrev = new JButton("< Шаг назад");
        btnPrev.addActionListener(e -> callback.accept(ListIteratorController.Op.PREV));
        add(btnPrev);
        
        JButton btnNext = new JButton("Шаг вперед >");
        btnNext.addActionListener(e -> callback.accept(ListIteratorController.Op.NEXT));
        add(btnNext) ;
        
        JButton btnEnd = new JButton("В конец >|");
        btnEnd.addActionListener(e -> callback.accept(ListIteratorController.Op.TO_END));
        add(btnEnd);
    }
}
