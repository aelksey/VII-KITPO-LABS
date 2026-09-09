package gui;

import core.CustomList;
import inface.UserTypeInterface;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class ListVisualizer<T> extends JPanel {

    private CustomList<T> list;
    private UserTypeInterface<T> userType;

    // Константы для отрисовки
    private static final int NODE_WIDTH = 90;
    private static final int NODE_HEIGHT = 40;
    private static final int HORIZONTAL_GAP = 60;
    private static final int LEVEL_HEIGHT = 25;
    private static final int START_X = 30;
    private static final int START_Y = 150; // Оставляем место сверху для дуг верхних уровней

    // Цвета для разных уровней стрелок
    private final Color[] levelColors = {
        Color.BLACK,         // L0
        new Color(0, 128, 0), // L1 (Зеленый)
        Color.BLUE,          // L2 (Синий)
        Color.RED,           // L3 (Красный)
        new Color(128, 0, 128) // L4 (Фиолетовый)
    };

    public ListVisualizer(CustomList<T> list, UserTypeInterface<T> userType) {
        this.list = list;
        this.userType = userType;
        setBackground(Color.WHITE);
    }

    public void setList(CustomList<T> list) {
        this.list = list;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (list == null || list.getSize() == 0) {
            g.drawString("Список пуст", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = list.getSize();
        
        // Массивы для хранения координат центров узлов, чтобы потом рисовать стрелки
        int[] nodeX = new int[size];
        
        // 1. Отрисовка самих узлов (нод)
        for (int i = 0; i < size; i++) {
            int x = START_X + i * (NODE_WIDTH + HORIZONTAL_GAP);
            int y = START_Y;
            nodeX[i] = x;

            T data = list.get(i);
            String dataStr = (userType != null) ? userType.toString(data) : (data != null ? data.toString() : "null");

            // Рисуем прямоугольник ноды
            g2.setColor(new Color(230, 240, 255));
            g2.fillRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 10, 10);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRoundRect(x, y, NODE_WIDTH, NODE_HEIGHT, 10, 10);

            // Текст внутри ноды
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(dataStr, x + 10, y + 24);

            // Индекс над нодой
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(Color.GRAY);
            g2.drawString("Индекс: " + i, x, y - 5);
        }

        // 2. Отрисовка связей (уровней) через Reflection API
        // Поскольку Node и массив next приватные, извлечем структуру для визуализации
        try {
            Object currentHead = getPrivateField(list, "head");
            Object current = currentHead;
            int sourceIdx = 0;

            while (current != null) {
                Object[] nextArray = (Object[]) getPrivateField(current, "next");
                
                // Проходим по всем уровням текущей ноды
                for (int level = 0; level < nextArray.length; level++) {
                    Object targetNode = nextArray[level];
                    if (targetNode == null) continue;

                    // Ищем индекс целевой ноды в списке
                    int targetIdx = findNodeIndex(currentHead, targetNode);
                    if (targetIdx != -1) {
                        drawLink(g2, sourceIdx, targetIdx, level, nodeX);
                    }
                }
                current = nextArray[0]; // Переход к следующему элементу по L0
                sourceIdx++;
            }
        } catch (Exception e) {
            g2.setColor(Color.RED);
            g2.drawString("Ошибка чтения структуры: " + e.getMessage(), 20, 20);
        }
    }

    // Метод отрисовки стрелки/дуги между узлами
    private void drawLink(Graphics2D g2, int src, int dst, int level, int[] nodeX) {
        Color color = level < levelColors.length ? levelColors[level] : Color.ORANGE;
        g2.setColor(color);

        int startX = nodeX[src] + NODE_WIDTH / 2;
        int endX = nodeX[dst] + NODE_WIDTH / 2;

        if (level == 0) {
            // Нулевой уровень — прямая стрелка между соседними элементами
            int y = START_Y + NODE_HEIGHT / 2;
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(nodeX[src] + NODE_WIDTH, y, nodeX[dst], y);
            drawArrowHead(g2, nodeX[dst], y, true);
        } else {
            // Верхние уровни — рисуем дугу сверху, высота зависит от уровня
            int arcHeight = level * LEVEL_HEIGHT;
            int startY = START_Y;
            int endY = START_Y;

            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4f}, 0.0f));
            
            // Строим кривую (дугу)
            int ctrlX = (startX + endX) / 2;
            int ctrlY = START_Y - arcHeight;
            
            // Для простоты восприятия можно сделать П-образную линию или плавную дугу:
            g2.drawLine(startX, startY, startX, ctrlY);
            g2.drawLine(startX, ctrlY, endX, ctrlY);
            g2.drawLine(endX, ctrlY, endX, endY);
            
            // Направление стрелки вниз к целевому элементу
            g2.setStroke(new BasicStroke(1.5f));
            drawArrowHead(g2, endX, endY, false);
            
            // Подпись уровня над линией
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            g2.drawString("L" + level, ctrlX - 6, ctrlY - 3);
        }
    }

    private void drawArrowHead(Graphics2D g2, int x, int y, boolean horizontal) {
        int[] xPoints;
        int[] yPoints;
        if (horizontal) {
            xPoints = new int[]{x, x - 8, x - 8};
            yPoints = new int[]{y, y - 4, y + 4};
        } else {
            xPoints = new int[]{x, x - 4, x + 4};
            yPoints = new int[]{y, y - 8, y - 8};
        }
        g2.fillPolygon(xPoints, yPoints, 3);
    }

    // Рефлексия для обхода приватной инкапсуляции структуры ради визуализации
    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private int findNodeIndex(Object headNode, Object targetNode) throws Exception {
        Object current = headNode;
        int idx = 0;
        while (current != null) {
            if (current == targetNode) return idx;
            Object[] nextArray = (Object[]) getPrivateField(current, "next");
            current = nextArray[0];
            idx++;
        }
        return -1;
    }
}
