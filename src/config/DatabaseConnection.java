package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // Ganti dengan nama database yang benar
    private static final String URL = "jdbc:mysql://localhost:3306/klinik?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() throws SQLException {
        try {
            // Pastikan driver terload
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver loaded successfully!");
            
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully to: " + URL);
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL Driver not found!");
            System.err.println("Please add mysql-connector-java-8.x.x.jar to your classpath");
            throw new SQLException("Driver MySQL tidak ditemukan. Tambahkan JAR file MySQL Connector ke lib folder.", e);
        }
    }
}