package com.jhonas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionDB {

    
    private static final String SERVER = "DESKTOP-HVD5TEJ\\SQLEXPRESS";
    private static final String DATABASE = "InventarioVideojuegosDB";
    private static final String USER = "sa";
    private static final String PASSWORD = "sql"; 
    private static final Logger LOGGER = Logger.getLogger(ConexionDB.class.getName());


    public static Connection getConnection() {
        
        String connectionUrl = String.format(
            "jdbc:sqlserver://%s;databaseName=%s;trustServerCertificate=true;",
            SERVER,
            DATABASE
        );

        try {
          
            Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falla al conectar a la base de datos.", e);
            return null;
        }
    }
}