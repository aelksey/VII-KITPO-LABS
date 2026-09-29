package gui;

import core.CustomList;
import inface.UserTypeInterface;
import inface.TraverseStrategyInterface;
import traverse.LinearTraverseStrategy;
import javax.swing.*;
import java.awt.*;

//Каждый прямоугольник массива - узел
public class ListVisualizer<T> extends JPanel {
    private CustomList<T> list;
    private final UserTypeInterface<T> type;
    private TraverseStrategyInterface<T> traversal = new LinearTraverseStrategy<>();
    private static final int STEP = 450, ROW = 62, OBJECT_WIDTH = 285;

    public ListVisualizer(CustomList<T> list, UserTypeInterface<T> type) {
        this.list = list; this.type = type; setBackground(Color.WHITE);
    }
    public void setTraversal(TraverseStrategyInterface<T> traversal) {
        this.traversal = java.util.Objects.requireNonNull(traversal); repaint();
    }
    public void setList(CustomList<T> list) { this.list = list; revalidate(); repaint(); }
    @Override public Dimension getPreferredSize() {
        return new Dimension(Math.max(700, 40 + list.getNodeCount() * STEP),
                Math.max(230, 100 + list.getBlockCapacity() * ROW));
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.DARK_GRAY);
            var blocks = list.getBlocks();
            int physical = 0;
            for (int b = 0; b < blocks.size(); b++) {
                int x = 20 + b * STEP;
                var values = blocks.get(b);
                int height = list.getBlockCapacity() * ROW + 15;
                g.setColor(new Color(225, 237, 250));
                g.fillRoundRect(x, 55, 90, height, 10, 10);
                g.setColor(Color.DARK_GRAY); g.drawRoundRect(x, 55, 90, height, 10, 10);
                g.drawString("Узел " + b, x, 23);
                g.drawString("Массив " + values.size() + "/" + list.getBlockCapacity(), x, 43);
                if (b + 1 < blocks.size()) {
                    g.drawString("next", x + 175, 18);
                    arrow(g, x + 95, 25, x + STEP - 10, 25);
                } else g.drawString("next -> null", x + 130, 25);
                for (int slot = 0; slot < list.getBlockCapacity(); slot++) {
                    int y = 65 + slot * ROW;
                    g.setColor(Color.WHITE); g.fillRect(x + 10, y, 70, 40);
                    g.setColor(Color.GRAY); g.drawRect(x + 10, y, 70, 40);
                    g.drawString("[" + slot + "]", x + 15, y + 24);
                    if (slot >= values.size()) {
                        g.drawString("", x + 52, y + 24);
                        continue;
                    }
                    int target = x + 130;
                    g.setColor(new Color(35, 90, 150)); arrow(g, x + 80, y + 20, target - 5, y + 20);
                    g.setColor(new Color(240, 248, 235)); g.fillRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8);
                    g.setColor(Color.DARK_GRAY); g.drawRoundRect(target, y - 3, OBJECT_WIDTH, 48, 8, 8);
                    g.drawString("Объект | лог. номер " + traversal.logicalIndex(physical++, list.getSize()), target + 7, y + 13);
                    String label = type.toString(values.get(slot));
                    while (label.length() > 1 && g.getFontMetrics().stringWidth(label) > OBJECT_WIDTH - 15)
                        label = label.substring(0, label.length() - 2) + "…";
                    g.drawString(label, target + 7, y + 33);
                }
            }
        } finally { g.dispose(); }
    }
    private void arrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(new int[]{x2, x2 - 7, x2 - 7}, new int[]{y2, y2 - 4, y2 + 4}, 3);
    }
}
