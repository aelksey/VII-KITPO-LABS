package nstu.rgr.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.List;
import nstu.rgr.core.CustomList;
import nstu.rgr.inface.UserTypeInterface;
import nstu.rgr.inface.TraverseStrategyInterface;

public class ListVisualizerView {
    private static final int STEP = 450;
    private static final int ROW = 62;
    private static final int OBJECT_WIDTH = 285;

    // Метод генерирует "кадр" (Bitmap) на основе текущего состояния списка
    public static <T> Bitmap generateRenderFrame(CustomList<T> customList, UserTypeInterface<T> type,
                                                 TraverseStrategyInterface<T> traversal,
                                                 ListIteratorController<T> iteratorController) {
        if (customList == null || type == null || traversal == null) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        // Вычисляем точные физические размеры будущего изображения
        int desiredWidth = Math.max(700, 40 + (customList.getNodeCount() * STEP));
        int desiredHeight = Math.max(230, 100 + (customList.getBlockCapacity() * ROW));

        // Создаем пустой холст в памяти
        Bitmap bitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE); // Белый фон подложки кадра

        // Настройка кистей (Paints)
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1.0f);

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(14f);
        textPaint.setColor(Color.DKGRAY);

        Paint boldTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        boldTextPaint.setTextSize(14f);
        boldTextPaint.setColor(Color.RED);
        boldTextPaint.setFakeBoldText(true);

        Path arrowPath = new Path();

        List<List<T>> blocks = customList.getBlocks();
        int physical = 0;

        for (int b = 0; b < blocks.size(); b++) {
            int x = 20 + (b * STEP);
            List<T> values = blocks.get(b);
            int height = (customList.getBlockCapacity() * ROW) + 15;

            // Отрисовка узла
            bgPaint.setColor(Color.rgb(225, 237, 250));
            RectF nodeRect = new RectF(x, 55, x + 90, 55 + height);
            canvas.drawRoundRect(nodeRect, 10, 10, bgPaint);
            strokePaint.setColor(Color.DKGRAY);
            strokePaint.setStrokeWidth(1.0f);
            canvas.drawRoundRect(nodeRect, 10, 10, strokePaint);

            canvas.drawText("Узел " + b, x, 23, textPaint);
            canvas.drawText("Массив " + values.size() + "/" + customList.getBlockCapacity(), x, 43, textPaint);

            if (b + 1 < blocks.size()) {
                canvas.drawText("next", x + 175, 18, textPaint);
                strokePaint.setColor(Color.DKGRAY);
                drawArrow(canvas, arrowPath, strokePaint, x + 95, 25, x + STEP - 10, 25);
            } else {
                canvas.drawText("next -> null", x + 130, 25, textPaint);
            }

            for (int slot = 0; slot < customList.getBlockCapacity(); slot++) {
                int y = 65 + (slot * ROW);
                bgPaint.setColor(Color.WHITE);
                canvas.drawRect(x + 10, y, x + 80, y + 40, bgPaint);
                strokePaint.setColor(Color.GRAY);
                strokePaint.setStrokeWidth(1.0f);
                canvas.drawRect(x + 10, y, x + 80, y + 40, strokePaint);
                canvas.drawText("[" + slot + "]", x + 15, y + 24, textPaint);

                if (slot >= values.size()) continue;

                int target = x + 130;
                strokePaint.setColor(Color.rgb(35, 90, 150));
                drawArrow(canvas, arrowPath, strokePaint, x + 80, y + 20, target - 5, y + 20);

                boolean isCurrentIterator = iteratorController != null && iteratorController.getCurrentPhysicalIndex(traversal) == physical;

                if (isCurrentIterator) {
                    bgPaint.setColor(Color.rgb(255, 230, 230));
                } else {
                    bgPaint.setColor(Color.rgb(240, 248, 235));
                }

                RectF objRect = new RectF(target, y - 3, target + OBJECT_WIDTH, y - 3 + 48);
                canvas.drawRoundRect(objRect, 8, 8, bgPaint);

                Paint currentTextPaint = textPaint;
                if (isCurrentIterator) {
                    strokePaint.setColor(Color.RED);
                    strokePaint.setStrokeWidth(2.0f);
                    canvas.drawRoundRect(objRect, 8, 8, strokePaint);
                    currentTextPaint = boldTextPaint;
                } else {
                    strokePaint.setColor(Color.DKGRAY);
                    strokePaint.setStrokeWidth(1.0f);
                    canvas.drawRoundRect(objRect, 8, 8, strokePaint);
                }

                canvas.drawText("Объект | лог. номер " + traversal.logicalIndex(physical, customList.getSize()), target + 7, y + 13, currentTextPaint);
                physical++;

                String label = type.toString(values.get(slot));
                if (currentTextPaint.measureText(label) > OBJECT_WIDTH - 15) {
                    while (currentTextPaint.measureText(label + "…") > OBJECT_WIDTH - 15 && label.length() > 1) {
                        label = label.substring(0, label.length() - 1);
                    }
                    label += "…";
                }
                canvas.drawText(label, target + 7, y + 33, currentTextPaint);
            }
        }
        return bitmap;
    }

    private static void drawArrow(Canvas canvas, Path arrowPath, Paint strokePaint, int x1, int y1, int x2, int y2) {
        canvas.drawLine(x1, y1, x2, y2, strokePaint);
        arrowPath.reset();
        arrowPath.moveTo(x2, y2);
        arrowPath.lineTo(x2 - 7, y2 - 4);
        arrowPath.lineTo(x2 - 7, y2 + 4);
        arrowPath.close();

        Paint arrowPaint = new Paint();
        arrowPaint.setColor(strokePaint.getColor());
        arrowPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(arrowPath, arrowPaint);
    }
}
