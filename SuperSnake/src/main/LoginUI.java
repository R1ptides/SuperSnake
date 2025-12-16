package main;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import database.UserAuth;

public class LoginUI {

    private final UserAuth auth = new UserAuth();
    private String loggedInUser = "Guest";

    public String showLoginWindow() {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Super Snake - Login");
        window.setMinWidth(350);

        Label title = new Label("Super Snake");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: limegreen;");

        Button loginBtn = new Button("Log In");
        Button registerBtn = new Button("Register");
        Button guestBtn = new Button("Continue as Guest");
        Button leaderboardBtn = new Button("View Leaderboard");

        styleButton(loginBtn);
        styleButton(registerBtn);
        styleButton(guestBtn);
        styleButton(leaderboardBtn);

        loginBtn.setOnAction(e -> loginWindow(window));
        registerBtn.setOnAction(e -> registerWindow());
        guestBtn.setOnAction(e -> {
            loggedInUser = "Guest";
            window.close();
        });
        leaderboardBtn.setOnAction(e -> openLeaderboard()); // calls class-level method

        VBox layout = new VBox(20, title, loginBtn, registerBtn, guestBtn, leaderboardBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(layout, 350, 350);

        // Press "L" to open leaderboard
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case L -> openLeaderboard();
            }
        });

        window.setScene(scene);
        window.showAndWait();

        return loggedInUser;
    }

    // LOGIN WINDOW — updated to close parent login window
    private void loginWindow(Stage parentWindow) {
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Log In");

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-text-fill: white;");
        TextField userField = new TextField();

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-text-fill: white;");
        PasswordField passField = new PasswordField();

        Button submit = new Button("Log In");
        styleButton(submit);

        Label status = new Label("");
        status.setStyle("-fx-text-fill: red;");

        submit.setOnAction(e -> {
            if (auth.login(userField.getText(), passField.getText())) {
                loggedInUser = userField.getText();

                // CLOSE THE LOGIN POPUP
                loginStage.close();

                // CLOSE MAIN LOGIN WINDOW
                parentWindow.close();

            } else {
                status.setText("Invalid username or password.");
            }
        });

        VBox layout = new VBox(15,
                userLabel, userField,
                passLabel, passField,
                submit, status
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black; -fx-padding: 20;");

        loginStage.setScene(new Scene(layout, 300, 300));
        loginStage.showAndWait();
    }

    // REGISTER WINDOW — now closes when registration succeeds
    private void registerWindow() {
        Stage regStage = new Stage();
        regStage.initModality(Modality.APPLICATION_MODAL);
        regStage.setTitle("Register");

        Label userLabel = new Label("Choose Username:");
        userLabel.setStyle("-fx-text-fill: white;");
        TextField userField = new TextField();

        Label passLabel = new Label("Choose Password:");
        passLabel.setStyle("-fx-text-fill: white;");
        PasswordField passField = new PasswordField();

        Button submit = new Button("Register");
        styleButton(submit);

        Label status = new Label("");
        status.setStyle("-fx-text-fill: red;");

        submit.setOnAction(e -> {
            if (auth.usernameExists(userField.getText())) {
                status.setText("Username already exists.");
                return;
            }

            if (auth.registerUser(userField.getText(), passField.getText())) {
                status.setStyle("-fx-text-fill: lime;");
                status.setText("Registered! Closing...");

                // CLOSE REGISTRATION WINDOW AFTER SUCCESS
                regStage.close();

            } else {
                status.setText("Registration error.");
            }
        });

        VBox layout = new VBox(15,
                userLabel, userField,
                passLabel, passField,
                submit, status
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black; -fx-padding: 20;");

        regStage.setScene(new Scene(layout, 300, 300));
        regStage.showAndWait();
    }

    /**
     * Class-level method: opens the JavaFX Leaderboard UI.
     * IMPORTANT: This must be declared at the class level (not inside another method).
     */
    private void openLeaderboard() {
    
        new LeaderboardUI().showLeaderboard(loggedInUser); 
    }

    private void styleButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: #222;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 5;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #444;" +
                "-fx-text-fill: lime;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 5;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #222;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 5;"
        ));
    }
}
