package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SnakeSegment {
    private double x;
    private double y;
    private double size;

    public SnakeSegment(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    // getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // controls the color and size of the snake's segments
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(x - size / 2, y - size / 2, size, size); 
    }
}
