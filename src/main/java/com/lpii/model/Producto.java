package com.lpii.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prod")
    private Integer idProd;
    
    @Column(name = "nom_prod", nullable = false, length = 150)
    private String nomProd;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cate", nullable = false)
    private Categoria categoria;
    
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventario> inventarios;
    
    public Producto() {
    }
    
    public Producto(String nomProd, Categoria categoria, Integer stockActual) {
        this.nomProd = nomProd;
        this.categoria = categoria;
        this.stockActual = stockActual;
    }
    
    // Getters y Setters
    public Integer getIdProd() {
        return idProd;
    }
    
    public void setIdProd(Integer idProd) {
        this.idProd = idProd;
    }
    
    public String getNomProd() {
        return nomProd;
    }
    
    public void setNomProd(String nomProd) {
        this.nomProd = nomProd;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public Integer getStockActual() {
        return stockActual;
    }
    
    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }
    
    public List<Inventario> getInventarios() {
        return inventarios;
    }
    
    public void setInventarios(List<Inventario> inventarios) {
        this.inventarios = inventarios;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "idProd=" + idProd +
                ", nomProd='" + nomProd + '\'' +
                ", stockActual=" + stockActual +
                '}';
    }
}