package com.jhonas;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        
        System.out.println("Iniciando la prueba de conexión a SQL Server...");

        // Usamos try-with-resources para que la conexión se cierre automáticamente
        try (Connection connection = ConexionDB.getConnection()) {
            
            // Verificación robusta: comprobamos que no sea nulo Y que esté activo
            if (connection != null && connection.isValid(5)) { 
                System.out.println("=========================================");
                System.out.println("✅ ¡CONEXIÓN EXITOSA!");
                System.out.println("Base de Datos: " + connection.getMetaData().getURL());
                System.out.println("=========================================");
            } else if (connection == null) {
                // El error ya fue manejado y logeado en ConexionDB.java
                System.out.println("❌ CONEXIÓN FALLIDA. Revisa la consola/log para detalles del error.");
            }
            // Si la conexión no es null pero no es válida, la BD está inaccesible temporalmente.
            
        } catch (SQLException e) {
            System.err.println("Error al obtener metadata o cerrar la conexión.");
            e.printStackTrace();
        }
    }
}