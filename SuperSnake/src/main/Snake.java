package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.stage.Stage;

public class Snake extends Application {

    // Snake head as an object
    private SnakeSegment head;

    // Mouse position
    private double mouseX = 400;
    private double mouseY = 300;

    // Movement speed (pixels per frame, might be adjustable later)
    private double speed = 3.0;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create the snake head at the center
        head = new SnakeSegment(400, 300, 20);

        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 800, 600, Color.BLACK);

        // Track mouse position
        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        stage.setTitle("Super Snake");
        stage.setScene(scene);
        stage.show();

        // Game loop (~60 fps)
        new AnimationTimer() {
            public void handle(long now) {
                update();
                draw(gc);
            }
        }.start();
    }

    private void update() {
        double dx = mouseX - head.getX();
        double dy = mouseY - head.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Move towards the mouse
        if (distance > 1) {
            head.setX(head.getX() + (dx / distance) * speed);
            head.setY(head.getY() + (dy / distance) * speed);
        }
    }

    private void draw(GraphicsContext gc) {
        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // Draw the snake head 
        head.draw(gc);
    }

    public static void main(String[] args) {
        launch();
    }
}
