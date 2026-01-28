package com.lpii.model;

public enum FrecuenciaCompra {
    DIARIO("Diario"),
    INTERDIARIO("Interdiario"),
    SEMANAL("Semanal"),
    POR_STOCK("Por Stock");
    
    private final String descripcion;
    
    FrecuenciaCompra(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}