package application;

import ui.App;

/**
 * Non-JavaFX entry point for fat JAR packaging.
 * JavaFX Application subclass cannot be the main class in a fat JAR
 * due to module system restrictions — this launcher bypasses that.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
