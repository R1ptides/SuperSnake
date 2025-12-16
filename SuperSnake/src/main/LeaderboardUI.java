package main;

import database.DatabaseLeaderboard;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.util.List;

public class LeaderboardUI {

    public void showLeaderboard(String currentUser) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Leaderboard");

        DatabaseLeaderboard db = new DatabaseLeaderboard();

        List<DatabaseLeaderboard.LeaderboardEntry> top10 = db.getTop10();
        DatabaseLeaderboard.LeaderboardEntry userBest = db.getUserBest(currentUser);

        Label title = new Label("Top 10 Players");
        title.setStyle("-fx-text-fill: lime; -fx-font-size: 22;");

        VBox listBox = new VBox(8);
        listBox.setAlignment(Pos.CENTER);

        int rank = 1;
        for (DatabaseLeaderboard.LeaderboardEntry e : top10) {
            Label entry = new Label(
                rank + ". " + e.username +
                " - Score: " + e.score +
                " | Time: " + e.time_elapsed      // ⬅ FIXED
            );
            entry.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
            listBox.getChildren().add(entry);
            rank++;
        }

        // User personal score
        Label personal = new Label();
        if (userBest != null) {
            personal.setText(
                "Your Best: " + userBest.username +
                " | Score: " + userBest.score +
                " | Time: " + userBest.time_elapsed  // ⬅ FIXED
            );
            personal.setStyle("-fx-text-fill: cyan; -fx-font-size: 16;");
        } else {
            personal.setText("You have no recorded scores yet.");
            personal.setStyle("-fx-text-fill: gray; -fx-font-size: 14;");
        }

        VBox layout = new VBox(20, title, listBox, personal);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black; -fx-padding: 20;");

        window.setScene(new Scene(layout, 400, 500));
        window.show();
    }
}
