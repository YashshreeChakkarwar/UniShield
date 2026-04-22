package com.unishield;

/**
 * ============================================================
 * LAUNCHER - Workaround for JavaFX module system
 * ============================================================
 * In Java 11+, launching a class that extends Application
 * directly can fail without proper module configuration.
 * This launcher class acts as a bridge — it doesn't extend
 * Application, so Java won't check for JavaFX modules
 * before entering main(). It then delegates to MainApp.
 */
public class Launcher {

    public static void main(String[] args) {
        // Delegate to the actual JavaFX Application class
        MainApp.main(args);
    }
}
