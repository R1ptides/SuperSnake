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
    private double speed = 3.0;          //May be adjustable using difficulty options in the future
    private final double SEGMENT_DISTANCE = 10;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private boolean gameOver = false;
    
    private Food food;
    private int score = 0;
    
    private long startTime; 
    private String timeDisplay = "00:00";
    
    // private long lastGrowTime = 0; // TEMPORARY - controls how often the snake grows

    // NEW: screen manager to handle states (home, play, pause, game over)
    private Screens screenManager = new Screens();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        body.add(new SnakeSegment(400, 300, 20));

        food = new Food(15);
  
        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        // NEW: left-click starts the game (from home or after game over)
        scene.setOnMouseClicked(e -> {
            if (screenManager.isHome() || screenManager.isGameOver()) {
                screenManager.setState(Screens.GameState.PLAYING);
                resetGame();
            }
        });

        // NEW: key controls for pause/play and end game
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE -> {
                    if (screenManager.isPlaying()) {
                        screenManager.setState(Screens.GameState.PAUSED);
                    } else if (screenManager.isPaused()) {
                        screenManager.setState(Screens.GameState.PLAYING);
                    }
                }
                case X -> {
                    screenManager.setState(Screens.GameState.GAME_OVER);
                    gameOver = true;
                }
                default -> {}
            }
        });

        stage.setTitle("Super Snake");
        stage.setScene(scene);
        stage.show();
        
        startTime = System.currentTimeMillis();

        new AnimationTimer() {
            public void handle(long now) {
                // only update if the game is in PLAYING state
                if (screenManager.isPlaying() && !gameOver) {
                    //update(now); // OLD: included timed growth, now replaced with update() below
                    update();
                    updateTimer();
                    checkFoodCollision();
                    checkCollisions();
                }

                // if snake dies during playing, switch to game over
                if (gameOver && screenManager.isPlaying()) {
                    screenManager.setState(Screens.GameState.GAME_OVER);
                }

                draw(gc);
            }
        }.start();
    }

    // NEW: reset game when restarting from home/game over
    private void resetGame() {
        score = 0;
        gameOver = false;
        body.clear();
        body.add(new SnakeSegment(400, 300, 20));
        food.respawn();
        startTime = System.currentTimeMillis();
        timeDisplay = "00:00";
    }

    private void updateTimer() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        int totalSeconds = (int) (elapsedMillis / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        timeDisplay = String.format("%02d:%02d", minutes, seconds);
    }
    
        
        
    // OLD VERSION (kept for reference):
    /*
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
            //pythagorean theorem to calculate the trajectory of the snake based on the mouse's position
            double segmentDist = Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy));
            
            if (segmentDist > SEGMENT_DISTANCE) {   //checks and ensures that the segments don't disconnect or deviate
                double moveX = (px - cx) / segmentDist * (segmentDist - SEGMENT_DISTANCE);
                double moveY = (py - cy) / segmentDist * (segmentDist - SEGMENT_DISTANCE);
                curr.setX(cx + moveX);
                curr.setY(cy + moveY);
            }
        }

        // Grow snake by 1 segment every 2 seconds (temporary until food is implemented)
        if ((now - lastGrowTime) > 2_000_000_000L) {
            grow();
            lastGrowTime = now;
        }
    }
    */

    private void update() {
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
            //pythagorean theorem to calculate the trajectory of each respective segment based on the position of the one before it
            double segmentDist = Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy));
            
            if (segmentDist > SEGMENT_DISTANCE) {   //checks and ensures that the segments don't disconnect or deviate
                double moveX = (px - cx) / segmentDist * (segmentDist - SEGMENT_DISTANCE);
                double moveY = (py - cy) / segmentDist * (segmentDist - SEGMENT_DISTANCE);
                curr.setX(cx + moveX);
                curr.setY(cy + moveY);
            }
        }
    }

    private void checkFoodCollision() {
        SnakeSegment head = body.get(0);
        double dx = head.getX() - food.getX();
        double dy = head.getY() - food.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 10) { // if head close enough to food
            score += 1;
            grow();
            food.respawn();
        }
    }

    private void grow() {
        SnakeSegment last = body.get(body.size() - 1);
        body.add(new SnakeSegment(last.getX(), last.getY(), 20)); //pull coordinates of last segment and add new segment accordingly
    }

    private void checkCollisions() {
        SnakeSegment head = body.get(0);

        // Wall collision
        if (head.getX() < 0 || head.getX() > WIDTH || head.getY() < 0 || head.getY() > HEIGHT) {
            gameOver = true;
        }

        // Self collision (only if longer than 2 segments)
        if (body.size() > 2) {
            for (int i = 1; i < body.size(); i++) {
                SnakeSegment part = body.get(i);
                double dx = head.getX() - part.getX();
                double dy = head.getY() - part.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= 5) {
                    gameOver = true;
                    break;
                }
            }
        }
    }	

    private void draw(GraphicsContext gc) {      // Clears the screen and redraws it for every frame
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Home screen
        if (screenManager.isHome()) {
            gc.setFill(Color.LIMEGREEN);
            gc.fillText("Welcome to Super Snake!", WIDTH / 2.0 - 80, HEIGHT / 2.0 - 20);
            gc.setFill(Color.WHITE);
            gc.fillText("Left click to play", WIDTH / 2.0 - 55, HEIGHT / 2.0 + 10);
            gc.fillText("Press SPACE to pause/resume, X to end game", WIDTH / 2.0 - 130, HEIGHT / 2.0 + 40);
            return;
        }

        // Gameplay screen
        if (screenManager.isPlaying()) {
            food.draw(gc);
            for (SnakeSegment segment : body) {
                segment.draw(gc);
            }

            gc.setFill(Color.WHITE);
            gc.fillText("Score: " + score, 10, 20);
            gc.fillText("Time: " + timeDisplay, 100, 20);
        }

        // Pause screen
        if (screenManager.isPaused()) {
            food.draw(gc);
            for (SnakeSegment segment : body) {
                segment.draw(gc);
            }
            gc.setFill(Color.YELLOW);
            gc.fillText("PAUSED", WIDTH / 2.0 - 30, HEIGHT / 2.0);
            gc.setFill(Color.WHITE);
            gc.fillText("Press SPACE to resume", WIDTH / 2.0 - 70, HEIGHT / 2.0 + 25);
        }

        // Game over screen
        if (screenManager.isGameOver()) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER", WIDTH / 2.0 - 40, HEIGHT / 2.0);
            gc.setFill(Color.WHITE);
            gc.fillText("Final Score: " + score, WIDTH / 2.0 - 45, HEIGHT / 2.0 + 25);
            gc.fillText("Time Elapsed: " + timeDisplay, WIDTH / 2.0 - 60, HEIGHT / 2.0 + 50);
            gc.fillText("Click to return to home", WIDTH / 2.0 - 70, HEIGHT / 2.0 + 75);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
