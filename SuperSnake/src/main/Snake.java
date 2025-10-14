package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Snake extends Application {

    private ArrayList<SnakeSegment> body = new ArrayList<>();
    private double mouseX = 400;
    private double mouseY = 300;
    private double speed = 3.0;

    private final double SEGMENT_DISTANCE = 10;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private boolean gameOver = false;
    private long lastGrowTime = 0; // controls how often snake grows (temporary until scoring and food is implemented)

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Start with just one segment (the head)
        body.add(new SnakeSegment(400, 300, 20));

        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        stage.setTitle("Super Snake");
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            public void handle(long now) {
                if (!gameOver) {
                    update(now);
                    checkCollisions();
                }
                draw(gc);
            }
        }.start();
    }

    private void update(long now) {
        SnakeSegment head = body.get(0);

        // Move head toward mouse
        double dx = mouseX - head.getX();
        double dy = mouseY - head.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 1) {
            head.setX(head.getX() + (dx / distance) * speed);
            head.setY(head.getY() + (dy / distance) * speed);
        }

        // Move each body segment to follow the one before it
        for (int i = body.size() - 1; i > 0; i--) {
            SnakeSegment prev = body.get(i - 1);
            SnakeSegment curr = body.get(i);

            double px = prev.getX();
            double py = prev.getY();
            double cx = curr.getX();
            double cy = curr.getY();

            double segmentDist = Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy));
            if (segmentDist > SEGMENT_DISTANCE) {
                double moveX = (px - cx) / segmentDist * (segmentDist - SEGMENT_DISTANCE);
                double moveY = (py - cy) / segmentDist * (segmentDist - SEGMENT_DISTANCE);
                curr.setX(cx + moveX);
                curr.setY(cy + moveY);
            }
        }

        // Grow snake every 2 seconds (temporary until food is implemented)
        if ((now - lastGrowTime) > 2_000_000_000L) {
            grow();
            lastGrowTime = now;
        }
    }

    private void grow() {
        SnakeSegment last = body.get(body.size() - 1);
        body.add(new SnakeSegment(last.getX(), last.getY(), 20));
    }

    private void checkCollisions() {
        SnakeSegment head = body.get(0);

        // Wall collision
        if (head.getX() < 0 || head.getX() > WIDTH || head.getY() < 0 || head.getY() > HEIGHT) {
            gameOver = true;
        }

        // Self collision (only if longer than 4 segments)
        if (body.size() > 4) {
            for (int i = 1; i < body.size(); i++) {
                SnakeSegment part = body.get(i);
                double dx = head.getX() - part.getX();
                double dy = head.getY() - part.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < 5) {
                    gameOver = true;
                    break;
                }
            }
        }
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        for (SnakeSegment segment : body) {
            segment.draw(gc);
        }

        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER", WIDTH / 2.0 - 40, HEIGHT / 2.0);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
