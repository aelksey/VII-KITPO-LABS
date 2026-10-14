package gui;

import core.CustomList;
import inface.UserTypeInterface;
import inface.TraverseStrategyInterface;
import traverse.LinearTraverseStrategy;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ListVisualizer<T> extends JPanel {
    private CustomList<T> customList;
    private final UserTypeInterface<T> type;
    private ListIteratorController<T> iteratorController = null; // Опциональный контроллер
    private TraverseStrategyInterface<T> traversal = new LinearTraverseStrategy<>();
    
    private static final int STEP = 450;
    private static final int ROW = 62;
    private static final int OBJECT_WIDTH = 285;

    public ListIteratorController<T> getIteratorController() {
        return this.iteratorController;
    }

    public ListVisualizer(CustomList<T> customList, UserTypeInterface<T> type) {
        this.customList = customList;
        this.type = type;
        setBackground(Color.WHITE);
    }

    public void setIteratorController(ListIteratorController<T> controller) {
        this.iteratorController = controller;
        repaint();
    }

    public void setTraversal(TraverseStrategyInterface<T> traversal) {
        this.traversal = java.util.Objects.requireNonNull(traversal);
        repaint();
    }

    public void setList(CustomList<T> newList) {
        this.customList = newList;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
            Math.max(700, 40 + (customList.getNodeCount() * STEP)),
            Math.max(230, 100 + (customList.getBlockCapacity() * ROW))
        );
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            List<List<T>> blocks = customList.getBlocks();
            int physical = 0;
            
            for (int b = 0; b < blocks.size(); b++) {
                int x = 20 + (b * STEP);
                List<T> values = blocks.get(b);
                int height = (customList.getBlockCapacity() * ROW) + 15;
                
                g.setColor(new Color(225, 237, 250));
                g.fillRoundRect(x, 55, 90, height, 10, 10);
                g.setColor(Color.DARK_GRAY);
                g.drawRoundRect(x, 55, 90, height, 10, 10);
                g.drawString("Узел " + b, x, 23);
                g.drawString("Массив " + values.size() + "/" + customList.getBlockCapacity(), x, 43);
                
                if (b + 1 < blocks.size()) {
                    g.drawString("next", x + 175, 18);
                    arrow(g, x + 95, 25, x + STEP - 10, 25);
                } else {
                    g.drawString("next -> null", x + 130, 25);
                }
                
                for (int slot = 0; slot < customList.getBlockCapacity(); slot++) {
                    int y = 65 + (slot * ROW);
                    g.setColor(Color.WHITE);
                    g.fillRect(x + 10, y, 70, 40);
                    g.setColor(Color.GRAY);
                    g.drawRect(x + 10, y, 70, 40);
                    g.drawString("[" + slot + "]", x + 15, y + 24);
                    
                    if (slot >= values.size()) {
                        g.drawString("", x + 52, y + 24);
                        continue;
                    }
                    
                    int target = x + 130;
                    g.setColor(new Color(35, 90, 150));
                    arrow(g, x + 80, y + 20, target - 5, y + 20);
                    
                    // ПРОВЕРКА: Находится ли здесь сейчас итератор?
                    boolean isCurrentIterator = iteratorController != null && iteratorController.getCurrentPhysicalIndex() == physical;

                    if (isCurrentIterator) {
                        g.setColor(new Color(255, 230, 230)); // Нежно-красный фон
                    } else {
                        g.setColor(new Color(240, 248, 235)); // Стандартный зеленый фон
                    }
                    
                    g.fillRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8);
                    
                    if (isCurrentIterator) {
                        g.setColor(Color.RED); // Яркая рамка фокуса
                        g.setStroke(new BasicStroke(2.0f));
                        g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8);
                        g.setStroke(new BasicStroke(1.0f)); 
                        g.setFont(g.getFont().deriveFont(Font.BOLD));
                    } else {
                        g.setColor(Color.DARK_GRAY);
                        g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8);
                    }
                    
                    g.drawString("Объект | лог. номер " + traversal.logicalIndex(physical, customList.getSize()), target + 7, y + 13);
                    physical++;
                    
                    String label = type.toString(values.get(slot));
                    while (label.length() > 1 && g.getFontMetrics().stringWidth(label) > OBJECT_WIDTH - 15) {
                        label = label.substring(0, label.length() - 2) + "…";
                    }
                    g.drawString(label, target + 7, y + 33);
                    
                    g.setFont(g.getFont().deriveFont(Font.PLAIN)); // Сброс шрифта
                }
            }
        } finally {
            g.dispose();
        }
    }

    private void arrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(new int[]{x2, x2 - 7, x2 - 7}, new int[]{y2, y2 - 4, y2 + 4}, 3);
    }
}
