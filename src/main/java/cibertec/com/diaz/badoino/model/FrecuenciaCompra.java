package cibertec.com.diaz.badoino.model;

public enum FrecuenciaCompra {
    DIARIO("Diario"),
    INTERDIARIO("Interdiario"),
    SEMANAL("Semanal"),
    MENSUAL("Mensual"),
    TRIMESTRAL("Trimestral"),
    POR_STOCK("Por Stock");
    
    private final String descripcion;
    
    FrecuenciaCompra(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}