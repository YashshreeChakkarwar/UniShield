package com.unishield;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.HashMap;

/**
 * ============================================================
 * LOGIN CONTROLLER - Authentication Module
 * ============================================================
 * Simple role-based login system with in-memory credentials.
 * Supports ADMIN and ANALYST roles. Creates a styled login
 * screen using JavaFX components.
 */
public class LoginController {

    // ---- User roles ----
    public enum UserRole {
        ADMIN,      // Full access: can apply defenses
        ANALYST     // View-only: can monitor and analyze
    }

    // ---- Credential storage: username -> [password, role] ----
    private HashMap<String, String[]> credentials;

    // ---- Current session ----
    private UserRole currentRole;
    private String currentUser;

    // ---- Callback for successful login ----
    private Runnable onLoginSuccess;

    // ---- UI Components ----
    private Label errorLabel;

    // ---- Constructor ----
    public LoginController() {
        this.credentials = new HashMap<>();
        initializeCredentials();
    }

    // ============================================================
    // INITIALIZE CREDENTIALS - Set up in-memory user accounts
    // ============================================================
    private void initializeCredentials() {
        // Format: username -> [password, role]
        credentials.put("admin", new String[]{"admin123", "ADMIN"});
        credentials.put("analyst", new String[]{"analyst123", "ANALYST"});
        credentials.put("drsmith", new String[]{"cyber2024", "ADMIN"});
        credentials.put("student1", new String[]{"monitor01", "ANALYST"});
    }

    // ============================================================
    // CREATE LOGIN SCENE - Build the login UI
    // ============================================================
    public Scene createLoginScene(Stage primaryStage) {
        // ---- Main container ----
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #0a0e17;");
        root.setPrefSize(1200, 750);

        // ---- Login card container ----
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(50));
        loginCard.setMaxWidth(420);
        loginCard.setStyle(
            "-fx-background-color: #141b2d;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: #1e2a3a;" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 212, 255, 0.15), 30, 0, 0, 5);"
        );

        // ---- Shield icon (text-based) ----
        Label shieldIcon = new Label("🛡️");
        shieldIcon.setFont(Font.font(48));

        // ---- Title ----
        Label titleLabel = new Label("UniShield");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#00d4ff"));

        // ---- Subtitle ----
        Label subtitleLabel = new Label("Cyber Defense System — University Network");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitleLabel.setTextFill(Color.web("#6c7a8e"));

        // ---- Spacer ----
        VBox spacer = new VBox();
        spacer.setPrefHeight(10);

        // ---- Username field ----
        Label userLabel = new Label("USERNAME");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        userLabel.setTextFill(Color.web("#8892a4"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(42);
        usernameField.setStyle(
            "-fx-background-color: #0d1520;" +
            "-fx-text-fill: #e0e6f0;" +
            "-fx-border-color: #1e2a3a;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14;" +
            "-fx-prompt-text-fill: #4a5568;"
        );

        // ---- Password field ----
        Label passLabel = new Label("PASSWORD");
        passLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        passLabel.setTextFill(Color.web("#8892a4"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(42);
        passwordField.setStyle(
            "-fx-background-color: #0d1520;" +
            "-fx-text-fill: #e0e6f0;" +
            "-fx-border-color: #1e2a3a;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-size: 14;" +
            "-fx-prompt-text-fill: #4a5568;"
        );

        // ---- Error label ----
        errorLabel = new Label("");
        errorLabel.setFont(Font.font("Segoe UI", 13));
        errorLabel.setTextFill(Color.web("#ff4d4d"));

        // ---- Login button ----
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(320);
        loginButton.setPrefHeight(44);
        loginButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #00d4ff, #0099cc);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        // Hover effect
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #33ddff, #00b3e6);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #00d4ff, #0099cc);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));

        // ---- Login action ----
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            handleLogin(username, password);
        });

        // Also login on Enter key in password field
        passwordField.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            handleLogin(username, password);
        });

        // ---- Credential hints ----
        Label hintLabel = new Label("Demo: admin/admin123 or analyst/analyst123");
        hintLabel.setFont(Font.font("Segoe UI", 11));
        hintLabel.setTextFill(Color.web("#4a5568"));

        // ---- Assemble login card ----
        VBox usernameBox = new VBox(5, userLabel, usernameField);
        VBox passwordBox = new VBox(5, passLabel, passwordField);

        loginCard.getChildren().addAll(
            shieldIcon, titleLabel, subtitleLabel,
            spacer,
            usernameBox, passwordBox,
            errorLabel, loginButton,
            hintLabel
        );

        root.getChildren().add(loginCard);

        return new Scene(root, 1200, 750);
    }

    // ============================================================
    // HANDLE LOGIN - Authenticate user credentials
    // ============================================================
    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        // Check if username exists in our credential store
        if (!credentials.containsKey(username)) {
            errorLabel.setText("Invalid username or password.");
            return;
        }

        // Retrieve stored password and role
        String[] stored = credentials.get(username);
        String storedPassword = stored[0];
        String roleString = stored[1];

        // Verify password
        if (!storedPassword.equals(password)) {
            errorLabel.setText("Invalid username or password.");
            return;
        }

        // Authentication successful
        this.currentUser = username;
        this.currentRole = UserRole.valueOf(roleString);
        errorLabel.setText("");

        // Trigger the login success callback
        if (onLoginSuccess != null) {
            onLoginSuccess.run();
        }
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UserRole getCurrentRole() {
        return currentRole;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
}
