package cibertec.com.diaz.badoino.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
public class Inventario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nro_inventario")
    private Integer nroInventario;
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_prod", nullable = false)
    private Producto producto;
    
    @Column(name = "costo_ingreso", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoIngreso;
    
    @Column(name = "motivo_ingreso", nullable = false, length = 200)
    private String motivoIngreso;
    
    public Inventario() {
    }
    
    public Inventario(Producto producto, BigDecimal costoIngreso, String motivoIngreso) {
        this.fecha = LocalDateTime.now();
        this.producto = producto;
        this.costoIngreso = costoIngreso;
        this.motivoIngreso = motivoIngreso;
    }
    
    // Getters y Setters
    public Integer getNroInventario() {
        return nroInventario;
    }
    
    public void setNroInventario(Integer nroInventario) {
        this.nroInventario = nroInventario;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public BigDecimal getCostoIngreso() {
        return costoIngreso;
    }
    
    public void setCostoIngreso(BigDecimal costoIngreso) {
        this.costoIngreso = costoIngreso;
    }
    
    public String getMotivoIngreso() {
        return motivoIngreso;
    }
    
    public void setMotivoIngreso(String motivoIngreso) {
        this.motivoIngreso = motivoIngreso;
    }
    
    @Override
    public String toString() {
        return "Inventario{" +
                "nroInventario=" + nroInventario +
                ", fecha=" + fecha +
                ", costoIngreso=" + costoIngreso +
                ", motivoIngreso='" + motivoIngreso + '\'' +
                '}';
    }
}