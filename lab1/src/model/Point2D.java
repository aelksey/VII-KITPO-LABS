package model;

// Тип Point2D
public class Point2D {
    private final double x;
    private final double y;

    public Point2D() { this.x = 0; this.y = 0; }
    public Point2D(double x, double y) { this.x = x; this.y = y; }

    public double getX() { return x; }
    public double getY() { return y; }

    // Расстояние до точки sqrt(x^2+y^2)
    public double getDistance() { return Math.sqrt(x * x + y * y); } 
}

