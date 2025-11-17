package com.jhonas.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jhonas.model.Videojuego;
import com.jhonas.service.VideojuegoService;

@Controller
public class VideojuegoController {
    
    @Autowired
    private VideojuegoService videojuegoService;
    
    // Página principal
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    // API REST Endpoints
    
    @GetMapping("/api/videojuegos")
    @ResponseBody
    public ResponseEntity<?> obtenerTodos() {
        try {
            List<Videojuego> videojuegos = videojuegoService.obtenerTodos();
            return ResponseEntity.ok(videojuegos);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al obtener videojuegos: " + e.getMessage()));
        }
    }
    
    @GetMapping("/api/videojuegos/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerPorId(@PathVariable int id) {
        try {
            Videojuego videojuego = videojuegoService.obtenerPorId(id);
            if (videojuego != null) {
                return ResponseEntity.ok(videojuego);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearRespuestaError("Videojuego no encontrado"));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al obtener videojuego: " + e.getMessage()));
        }
    }
    
    @PostMapping("/api/videojuegos")
    @ResponseBody
    public ResponseEntity<?> agregar(@RequestBody Videojuego videojuego) {
        try {
            videojuegoService.agregar(videojuego);
            return ResponseEntity.ok(crearRespuestaExito("Videojuego agregado exitosamente"));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al agregar videojuego: " + e.getMessage()));
        }
    }
    
    @PutMapping("/api/videojuegos/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Videojuego videojuego) {
        try {
            videojuego.setVideojuegoID(id);
            videojuegoService.actualizar(videojuego);
            return ResponseEntity.ok(crearRespuestaExito("Videojuego actualizado exitosamente"));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al actualizar videojuego: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/api/videojuegos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        try {
            videojuegoService.eliminar(id);
            return ResponseEntity.ok(crearRespuestaExito("Videojuego eliminado exitosamente"));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al eliminar videojuego: " + e.getMessage()));
        }
    }
    
    @GetMapping("/api/videojuegos/buscar")
    @ResponseBody
    public ResponseEntity<?> buscar(@RequestParam String q) {
        try {
            List<Videojuego> videojuegos = videojuegoService.buscar(q);
            return ResponseEntity.ok(videojuegos);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al buscar videojuegos: " + e.getMessage()));
        }
    }
    
    private Map<String, String> crearRespuestaExito(String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "success");
        respuesta.put("message", mensaje);
        return respuesta;
    }
    
    private Map<String, String> crearRespuestaError(String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("status", "error");
        respuesta.put("message", mensaje);
        return respuesta;
    }
}
