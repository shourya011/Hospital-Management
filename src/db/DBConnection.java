package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Singleton class for managing database connections
 * Ensures only one connection to the database exists at a time
 * Credentials are loaded from db.properties file
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    
    // Database configuration loaded from properties file
    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    // Static initializer to load properties
    static {
        loadProperties();
    }
    
    /**
     * Load database configuration from db.properties file
     */
    private static void loadProperties() {
        Properties props = new Properties();
        try {
            // Try to load from current working directory first
            FileInputStream fis = new FileInputStream("db.properties");
            props.load(fis);
            fis.close();
            
            DB_DRIVER = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            DB_URL = props.getProperty("db.url", "jdbc:mysql://127.0.0.1:3306/hospital_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
            DB_USER = props.getProperty("db.user", "root");
            DB_PASSWORD = props.getProperty("db.password", "root");
            
            System.out.println("✓ Database configuration loaded from db.properties");
        } catch (IOException e) {
            System.err.println("WARNING: db.properties not found! Using default values.");
            // Fallback to defaults
            DB_DRIVER = "com.mysql.cj.jdbc.Driver";
            DB_URL = "jdbc:mysql://127.0.0.1:3306/hospital_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
            DB_USER = "root";
            DB_PASSWORD = "root";
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private DBConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName(DB_DRIVER);
            
            // Establish connection
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Connected to hospital_db successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Make sure mysql-connector-java JAR is in the lib folder.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database!");
            System.err.println("Make sure MySQL is running and credentials are correct.");
            e.printStackTrace();
        }
    }
    
    /**
     * Get the singleton instance of DBConnection
     * @return DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    /**
     * Get the active database connection
     * @return Connection object
     */
    public Connection getConnection() {
        return this.connection;
    }
    
    /**
     * Close the database connection
     */
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                System.out.println("✓ Database connection closed!");
            } catch (SQLException e) {
                System.err.println("ERROR: Failed to close database connection!");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Check if connection is still active
     * @return true if connection is valid, false otherwise
     */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
