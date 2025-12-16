package main;

import javafx.stage.Stage;
import database.DatabaseLeaderboard;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.Group;

// NEW IMPORTS FOR IMAGE + ROTATION
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

public class Snake extends Application {

    private ArrayList<SnakeSegment> body = new ArrayList<>();
    private double mouseX = 400;
    private double mouseY = 300;
    private double speed = 4.0;
    private final double SEGMENT_DISTANCE = 10;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private boolean gameOver = false;

    private Food food;
    private int score = 0;

    private long startTime;
    private String timeDisplay = "00:00";
    
    private Screens screenManager = new Screens();
    private String loggedInUser = "Guest";

    private Image headImage;

    @Override
    public void start(Stage stage) {

        headImage = new Image("file:C:/Users/flyin/eclipse-workspace/SuperSnake/src/resources/SnakeHead.png");

        LoginUI login = new LoginUI();
        loggedInUser = login.showLoginWindow();
        System.out.println("Logged in as: " + loggedInUser);

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

        scene.setOnMouseClicked(e -> {
            if (screenManager.isHome()) {
                double mx = e.getX();
                double my = e.getY();

                if (mx >= WIDTH/2 - 90 && mx <= WIDTH/2 + 90 &&
                    my >= HEIGHT/2 + 50 && my <= HEIGHT/2 + 80) {
                    new LeaderboardUI().showLeaderboard(loggedInUser);
                }

                screenManager.setState(Screens.GameState.PLAYING);
                resetGame();
            }

            if (screenManager.isGameOver()) {
                screenManager.setState(Screens.GameState.HOME);
            }
        });

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE -> {
                    if (screenManager.isPlaying()) screenManager.setState(Screens.GameState.PAUSED);
                    else if (screenManager.isPaused()) screenManager.setState(Screens.GameState.PLAYING);
                }
                case X -> {
                    screenManager.setState(Screens.GameState.GAME_OVER);
                    gameOver = true;
                }
                case L -> new LeaderboardUI().showLeaderboard(loggedInUser);
                default -> {}
            }
        });

        stage.setTitle("Super Snake");
        stage.setScene(scene);
        stage.show();

        startTime = System.currentTimeMillis();

        new AnimationTimer() {
            public void handle(long now) {
                if (screenManager.isPlaying() && !gameOver) {
                    update();
                    updateTimer();
                    checkFoodCollision();
                    checkCollisions();
                }

                if (gameOver && screenManager.isPlaying()) {
                    screenManager.setState(Screens.GameState.GAME_OVER);

                    if (!loggedInUser.equalsIgnoreCase("Guest")) {
                        DatabaseLeaderboard db = new DatabaseLeaderboard();
                        db.saveScore(loggedInUser, score, timeDisplay);
                    }
                }

                draw(gc);
            }
        }.start();
    }

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

    private void update() {
        SnakeSegment head = body.get(0);

        double dx = mouseX - head.getX();
        double dy = mouseY - head.getY();
        double distance = Math.sqrt(dx*dx + dy*dy);

        if (distance > 1) {
            head.setX(head.getX() + (dx / distance) * speed);
            head.setY(head.getY() + (dy / distance) * speed);
        }

        for (int i = body.size() - 1; i > 0; i--) {
            SnakeSegment prev = body.get(i - 1);
            SnakeSegment curr = body.get(i);

            double px = prev.getX();
            double py = prev.getY();
            double cx = curr.getX();
            double cy = curr.getY();

            double segmentDist = Math.sqrt((px - cx)*(px - cx) + (py - cy)*(py - cy));

            double desiredDistance = (i == 1 ? 25 : SEGMENT_DISTANCE);

            if (segmentDist > desiredDistance) {
                double moveX = (px - cx) / segmentDist * (segmentDist - desiredDistance);
                double moveY = (py - cy) / segmentDist * (segmentDist - desiredDistance);
                curr.setX(cx + moveX);
                curr.setY(cy + moveY);
            }
        }
    }

    private void checkFoodCollision() {
        SnakeSegment head = body.get(0);
        double dx = head.getX() - food.getX();
        double dy = head.getY() - food.getY();
        double distance = Math.sqrt(dx*dx + dy*dy);

        double eatDistance = 12;
        if (distance < eatDistance) {
            score++;
            grow();

            int tries = 0;
            double minDist = 40; 
            food.respawn();
            while (tries < 10) {
                double dx2 = head.getX() - food.getX();
                double dy2 = head.getY() - food.getY();
                double dist2 = Math.sqrt(dx2*dx2 + dy2*dy2);
                if (dist2 >= minDist) break;
                food.respawn();
                tries++;
            }
        }
    }

    private void grow() {
        double FIRST_SEGMENT_OFFSET = 20 ;
        double offset = SEGMENT_DISTANCE;

        SnakeSegment last = body.get(body.size() - 1);
        double newX = last.getX();
        double newY = last.getY();

        if (body.size() == 1) {
           
            SnakeSegment head = last;

            double dx = mouseX - head.getX();
            double dy = mouseY - head.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);

            if (dist != 0) {
                newX = head.getX() - (dx / dist) * FIRST_SEGMENT_OFFSET;
                newY = head.getY() - (dy / dist) * FIRST_SEGMENT_OFFSET;
            } else {
                newX = head.getX() - FIRST_SEGMENT_OFFSET;
                newY = head.getY();
            }

        } else { 
            SnakeSegment secondLast = body.get(body.size() - 2);

            double dx = last.getX() - secondLast.getX();
            double dy = last.getY() - secondLast.getY();
            double dist = Math.sqrt(dx*dx + dy*dy);

            if (dist != 0) {
                newX += (dx / dist) * offset;
                newY += (dy / dist) * offset;
            }
        }

        body.add(new SnakeSegment(newX, newY, 20));
    }




    private void checkCollisions() {
        SnakeSegment head = body.get(0);

        if (head.getX() < 0 || head.getX() > WIDTH ||
            head.getY() < 0 || head.getY() > HEIGHT) {
            gameOver = true;
        }

        for (int i = 1; i < body.size(); i++) {
            SnakeSegment part = body.get(i);
            double dx = head.getX() - part.getX();
            double dy = head.getY() - part.getY();
            if (Math.sqrt(dx*dx + dy*dy) <= 5) {
                gameOver = true;
                break;
            }
        }
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        if (screenManager.isHome()) {
            gc.setFill(Color.LIMEGREEN);
            gc.fillText("Welcome, " + loggedInUser + "!", WIDTH/2 - 80, HEIGHT/2 - 60);

            gc.setFill(Color.WHITE);
            gc.fillText("Left click to play", WIDTH/2 - 55, HEIGHT/2 - 10);

            gc.setFill(Color.LIGHTBLUE);
            gc.fillText("View Leaderboard (L)", WIDTH/2 - 75, HEIGHT/2 + 60);
            return;
        }

        if (screenManager.isPlaying()) {
            food.draw(gc);

            SnakeSegment head = body.get(0);
            double angle = Math.toDegrees(Math.atan2(mouseY - head.getY(), mouseX - head.getX()));
            drawRotatedImage(gc, headImage, angle, head.getX(), head.getY());

            for (int i = 1; i < body.size(); i++) {
                body.get(i).draw(gc);
            }

            gc.setFill(Color.WHITE);
            gc.fillText("Score: " + score, 10, 20);
            gc.fillText("Time: " + timeDisplay, 100, 20);
            gc.fillText("Player: " + loggedInUser, 10, 40);
        }

        if (screenManager.isPaused()) {
            food.draw(gc);
            for (SnakeSegment segment : body) segment.draw(gc);
            gc.setFill(Color.YELLOW);
            gc.fillText("PAUSED", WIDTH/2 - 30, HEIGHT/2);
            gc.fillText("Press SPACE to resume", WIDTH/2 - 70, HEIGHT/2 + 25);
        }

        if (screenManager.isGameOver()) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER", WIDTH/2 - 40, HEIGHT/2);
            gc.setFill(Color.WHITE);
            gc.fillText("Final Score: " + score, WIDTH/2 - 45, HEIGHT/2 + 25);
            gc.fillText("Time Elapsed: " + timeDisplay, WIDTH/2 - 60, HEIGHT/2 + 50);
            gc.fillText("Click to return to home", WIDTH/2 - 70, HEIGHT/2 + 75);
        }
    }

    private void drawRotatedImage(GraphicsContext gc, Image img, double angle, double x, double y) {
        double size = 30;

        gc.save();
        Rotate r = new Rotate(angle, x, y);
        gc.transform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

        
        gc.drawImage(img, 
            x - size / 2,  
            y - size / 2,  
            size,          
            size           
        );

        gc.restore();
    }


    public static void main(String[] args) {
        launch();
    }
}
