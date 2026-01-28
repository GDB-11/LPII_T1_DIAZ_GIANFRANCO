package com.lpii.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categoria")
public class Categoria implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cate")
    private Integer idCate;
    
    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "frecuencia_compra", nullable = false, length = 50)
    private FrecuenciaCompra frecuenciaCompra;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;
    
    public Categoria() {
    }
    
    public Categoria(String descripcion, FrecuenciaCompra frecuenciaCompra) {
        this.descripcion = descripcion;
        this.frecuenciaCompra = frecuenciaCompra;
    }
    
    // Getters y Setters
    public Integer getIdCate() {
        return idCate;
    }
    
    public void setIdCate(Integer idCate) {
        this.idCate = idCate;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public FrecuenciaCompra getFrecuenciaCompra() {
        return frecuenciaCompra;
    }
    
    public void setFrecuenciaCompra(FrecuenciaCompra frecuenciaCompra) {
        this.frecuenciaCompra = frecuenciaCompra;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
    
    @Override
    public String toString() {
        return "Categoria{" +
                "idCate=" + idCate +
                ", descripcion='" + descripcion + '\'' +
                ", frecuenciaCompra=" + frecuenciaCompra +
                '}';
    }
}