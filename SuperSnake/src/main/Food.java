package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class Food {
    private double x;
    private double y;
    private double size;
    private final Random random = new Random();

    private final int WIDTH = 800;
    private final int HEIGHT = 600; 

    public Food(double size) {
        this.size = size;
        respawn(); // create first food location
    }

    // Draw food on screen
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x - size / 2, y - size / 2, size, size);  //size of snake segments
    }

    // Randomly place food somewhere on the board
    public void respawn() {
        x = random.nextInt(WIDTH - 40) + 20;
        y = random.nextInt(HEIGHT - 40) + 20;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
}
