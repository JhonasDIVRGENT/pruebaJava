package com.jhonas.model;

public class Videojuego {
    private int videojuegoID;
    private String titulo;
    private String genero;
    private String plataforma;
    private int stock;
    private double precio;
    
    public Videojuego() {
    }
    
    public Videojuego(int videojuegoID, String titulo, String genero, String plataforma, int stock, double precio) {
        this.videojuegoID = videojuegoID;
        this.titulo = titulo;
        this.genero = genero;
        this.plataforma = plataforma;
        this.stock = stock;
        this.precio = precio;
    }
    
    // Getters y Setters
    public int getVideojuegoID() {
        return videojuegoID;
    }
    
    public void setVideojuegoID(int videojuegoID) {
        this.videojuegoID = videojuegoID;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public String getPlataforma() {
        return plataforma;
    }
    
    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
