package main;

import java.util.Scanner;
import database.UserAuth;

public class LoginScreen {

    private UserAuth auth = new UserAuth();
    private Scanner scanner = new Scanner(System.in);

    private String loggedInUser = null;
    private boolean guestMode = false;

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isGuest() {
        return guestMode;
    }

    public void showMenu() {
        System.out.println("========== Super Snake ==========");
        System.out.println("1. Log In");
        System.out.println("2. Register");
        System.out.println("3. Continue as Guest");
        System.out.print("Select an option: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> loginScreen();
            case "2" -> registerScreen();
            case "3" -> {
                guestMode = true;
                loggedInUser = "Guest";
                System.out.println("Continuing as Guest...");
            }
            default -> {
                System.out.println("Invalid input. Try again.");
                showMenu();
            }
        }
    }

    private void loginScreen() {
        System.out.print("Enter Username: ");
        String user = scanner.nextLine();

        System.out.print("Enter Password: ");
        String pass = scanner.nextLine();

        if (auth.login(user, pass)) {
            loggedInUser = user;
            System.out.println("Login successful! Welcome " + user + "!");
        } else {
            System.out.println("Incorrect username or password.");
            loginScreen();
        }
    }

    private void registerScreen() {
        System.out.print("Choose Username: ");
        String user = scanner.nextLine();

        if (auth.usernameExists(user)) {
            System.out.println("Username already exists. Try another.");
            registerScreen();
            return;
        }

        System.out.print("Choose Password: ");
        String pass = scanner.nextLine();

        if (auth.registerUser(user, pass)) {
            System.out.println("Registration successful! You may now log in.");
        } else {
            System.out.println("Error registering. Try again.");
        }

        showMenu();
    }
}
