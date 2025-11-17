package com.jhonas.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jhonas.ConexionDB;
import com.jhonas.model.Videojuego;

@Service
public class VideojuegoService {
    
    public List<Videojuego> obtenerTodos() throws SQLException {
        List<Videojuego> videojuegos = new ArrayList<>();
        String query = "SELECT VideojuegoID, Titulo, Genero, Plataforma, Stock, Precio FROM Videojuegos ORDER BY VideojuegoID";
        
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Videojuego v = new Videojuego(
                    rs.getInt("VideojuegoID"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("Plataforma"),
                    rs.getInt("Stock"),
                    rs.getDouble("Precio")
                );
                videojuegos.add(v);
            }
        }
        return videojuegos;
    }
    
    public Videojuego obtenerPorId(int id) throws SQLException {
        String query = "{CALL sp_ObtenerVideojuegoPorID(?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setInt(1, id);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return new Videojuego(
                    rs.getInt("VideojuegoID"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("Plataforma"),
                    rs.getInt("Stock"),
                    rs.getDouble("Precio")
                );
            }
        }
        return null;
    }
    
    public void agregar(Videojuego videojuego) throws SQLException {
        String query = "{CALL sp_InsertarVideojuego(?, ?, ?, ?, ?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setString(1, videojuego.getTitulo());
            cstmt.setString(2, videojuego.getGenero());
            cstmt.setString(3, videojuego.getPlataforma());
            cstmt.setInt(4, videojuego.getStock());
            cstmt.setDouble(5, videojuego.getPrecio());
            
            cstmt.execute();
        }
    }
    
    public void actualizar(Videojuego videojuego) throws SQLException {
        String query = "{CALL sp_ActualizarVideojuego(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setInt(1, videojuego.getVideojuegoID());
            cstmt.setString(2, videojuego.getTitulo());
            cstmt.setString(3, videojuego.getGenero());
            cstmt.setString(4, videojuego.getPlataforma());
            cstmt.setInt(5, videojuego.getStock());
            cstmt.setDouble(6, videojuego.getPrecio());
            
            cstmt.execute();
        }
    }
    
    public void eliminar(int id) throws SQLException {
        String query = "{CALL sp_EliminarVideojuego(?)}";
        
        try (Connection conn = ConexionDB.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {
            
            cstmt.setInt(1, id);
            cstmt.execute();
        }
    }
    
    public List<Videojuego> buscar(String termino) throws SQLException {
        List<Videojuego> videojuegos = new ArrayList<>();
        String query = "SELECT VideojuegoID, Titulo, Genero, Plataforma, Stock, Precio " +
                      "FROM Videojuegos WHERE Titulo LIKE ? OR Genero LIKE ? OR Plataforma LIKE ? " +
                      "ORDER BY VideojuegoID";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String patron = "%" + termino + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);
            pstmt.setString(3, patron);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Videojuego v = new Videojuego(
                    rs.getInt("VideojuegoID"),
                    rs.getString("Titulo"),
                    rs.getString("Genero"),
                    rs.getString("Plataforma"),
                    rs.getInt("Stock"),
                    rs.getDouble("Precio")
                );
                videojuegos.add(v);
            }
        }
        return videojuegos;
    }
}
