package com.unishield;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * ============================================================
 * MAIN APP - JavaFX Application Entry Point
 * ============================================================
 * Entry point for the UniShield Cyber Defense System.
 * 
 * NOTE: This class does NOT extend Application directly.
 * Instead, it contains a static inner class (AppWindow) that
 * extends Application. This is a standard workaround for
 * Java 11+ where running a class that extends Application
 * directly fails without --module-path configuration.
 * 
 * By keeping main() in a non-Application class, the JVM
 * loads JavaFX from the classpath without module checks.
 */
public class MainApp {

    // ============================================================
    // INNER APPLICATION CLASS - The actual JavaFX window
    // ============================================================
    public static class AppWindow extends Application {

        private Stage primaryStage;
        private LoginController loginController;

        @Override
        public void start(Stage stage) {
            this.primaryStage = stage;

            // Configure the main window
            primaryStage.setTitle("UniShield — Graph-Based Cyber Defense System");
            primaryStage.setMinWidth(1100);
            primaryStage.setMinHeight(700);

            // Show login screen first
            showLoginScreen();

            primaryStage.show();
        }

        // ---- Show Login Screen ----
        private void showLoginScreen() {
            loginController = new LoginController();

            // Set callback for successful login
            loginController.setOnLoginSuccess(() -> {
                String user = loginController.getCurrentUser();
                LoginController.UserRole role = loginController.getCurrentRole();
                switchToDashboard(user, role);
            });

            Scene loginScene = loginController.createLoginScene(primaryStage);
            primaryStage.setScene(loginScene);
        }

        // ---- Switch to Dashboard ----
        private void switchToDashboard(String user, LoginController.UserRole role) {
            DashboardController dashboard = new DashboardController(user, role);

            // Set logout callback to return to login screen
            dashboard.setOnLogout(() -> showLoginScreen());

            Scene dashboardScene = dashboard.createDashboardScene(primaryStage);
            primaryStage.setScene(dashboardScene);
        }
    }

    // ============================================================
    // MAIN METHOD - Entry point (does NOT extend Application)
    // ============================================================
    public static void main(String[] args) {
        // Launch the JavaFX Application inner class
        Application.launch(AppWindow.class, args);
    }
}
