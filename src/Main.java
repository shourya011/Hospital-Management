import db.DBConnection;
import ui.MainDashboard;

import javax.swing.*;

/**
 * Main entry point for the Hospital Management System application
 * Initializes database connection and launches the GUI
 */
public class Main {
    public static void main(String[] args) {
        // Set Cross-Platform Look and Feel (Metal L&F)
        // Use getCrossPlatformLookAndFeelClassName() instead of getSystemLookAndFeelClassName()
        // because system L&F overrides button setBackground() and setForeground() colors
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }
        
        // Initialize database connection
        System.out.println("=".repeat(60));
        System.out.println("RBH - Hospital Management System");
        System.out.println("=".repeat(60));
        System.out.println("Initializing database connection...");
        
        DBConnection dbConnection = DBConnection.getInstance();
        
        if (!dbConnection.isConnected()) {
            System.err.println("FATAL: Could not establish database connection!");
            System.err.println("Make sure:");
            System.err.println("  1. MySQL server is running");
            System.err.println("  2. hospital_db database exists");
            System.err.println("  3. Credentials are correct (root:root)");
            System.err.println("  4. mysql-connector-java JAR is in lib/ folder");
            System.exit(1);
        }
        
        System.out.println("=".repeat(60));
        System.out.println("Launching application...");
        System.out.println("=".repeat(60));
        
        // Launch GUI on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(MainDashboard::new);
    }
}
