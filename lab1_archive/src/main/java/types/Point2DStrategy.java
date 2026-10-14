package types;

import inface.UserTypeInterface;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import model.Point2D;

// Реализация интерфейса UserType для типа Point2D
public class Point2DStrategy implements UserTypeInterface<Point2D> {
    @Override
    public Point2D randomValue(java.util.random.RandomGenerator random) {
        return new Point2D(random.nextInt(-10000, 10001) / 100.0,
                random.nextInt(-10000, 10001) / 100.0);
    }

    @Override
    public java.util.List<Point2D> sampleValues() {
        return java.util.List.of(new Point2D(3, 4), new Point2D(1, 1), new Point2D(0, 2), new Point2D(5, 12), new Point2D(-3, 4), new Point2D(0, 0));
    }
    @Override
    public String typeName() { return "2D-точка"; }

    @Override
    public Point2D create() { return new Point2D(); }

    @Override
    public Point2D clone(Point2D object) { return new Point2D(object.getX(), object.getY()); }

    @Override
    public Point2D parseValue(String ss) {
        String[] parts = ss.trim().split("\\s+");
        return new Point2D(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }

    @Override
    public Point2D readValue(InputStreamReader in) {
        // try-with-resources автоматически закроет br после выхода из блока
        try (java.io.BufferedReader br = new java.io.BufferedReader(in)) {
            String line = br.readLine();
            if (line == null) return null;
            return parseValue(line);
        } catch (Exception e) {
            // Логируем или обрабатываем ошибку при необходимости
            return null;
        }
    }


    @Override
    public int compare(Point2D o1, Point2D o2) {
        return Double.compare(o1.getDistance(), o2.getDistance());
    }

    @Override
    public String toString(Point2D object) {
        return String.format("Точка(%.2f; %.2f) [Дист: %.2f]", object.getX(), object.getY(), object.getDistance());
    }

    @Override
    public void writeValue(Point2D object, BufferedWriter writer) throws IOException {
        writer.write(object.getX() + " " + object.getY());
    }
}

