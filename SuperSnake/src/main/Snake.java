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

    // Snake position
    private double snakeX = 400;
    private double snakeY = 300;

    // Mouse position
    private double mouseX = 400;
    private double mouseY = 300;

    // Movement speed (pixels per frame, might be able to be adjusted in the future via difficulty options)
    private double speed = 3.0;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 800, 600, Color.BLACK);

        scene.setOnMouseMoved(e ->
            {mouseX = e.getX();
            mouseY = e.getY();});

        stage.setTitle("Super Snake");
        stage.setScene(scene);
        stage.show();

        // Game loop (runs ~60 times per second, which is 60 fps if your hardware allows it)
        new AnimationTimer() {
            public void handle(long now) {
                update();
                draw(gc);
            }
        }.start();
    }

    private void update() {
        double dx = mouseX - snakeX;
        double dy = mouseY - snakeY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        // Move towards the mouse if it's not already at the position of the mouse
        if (distance > 1) {
            snakeX += (dx / distance) * speed;
            snakeY += (dy / distance) * speed;
        }
    }

    // Draw snake
    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);    // background color, subject to change 
        gc.fillRect(0, 0, 800, 600);

        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(snakeX - 10, snakeY - 10, 20, 20); // snake head, subject to change
    }

    public static void main(String[] args) {
        launch();
    }
}
