package cibertec.com.diaz.badoino.controller;

import cibertec.com.diaz.badoino.controller.exceptions.IllegalOrphanException;
import cibertec.com.diaz.badoino.controller.exceptions.NonexistentEntityException;
import cibertec.com.diaz.badoino.model.Categoria;
import cibertec.com.diaz.badoino.model.Inventario;
import cibertec.com.diaz.badoino.model.Producto;
import cibertec.com.diaz.badoino.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador JPA para la entidad Producto
 */
public class ProductoJpaController {
    
    public void create(Producto producto) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Categoria categoria = producto.getCategoria();
            if (categoria != null) {
                categoria = em.getReference(categoria.getClass(), categoria.getIdCate());
                producto.setCategoria(categoria);
            }
            
            if (producto.getInventarios() == null) {
                producto.setInventarios(new ArrayList<>());
            }
            
            em.persist(producto);
            
            if (categoria != null) {
                categoria.getProductos().add(producto);
                em.merge(categoria);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al crear el producto", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public void edit(Producto producto) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Producto persistentProducto = em.find(Producto.class, producto.getIdProd());
            if (persistentProducto == null) {
                throw new NonexistentEntityException(
                    "El producto con id " + producto.getIdProd() + " no existe."
                );
            }
            
            Categoria categoriaOld = persistentProducto.getCategoria();
            Categoria categoriaNew = producto.getCategoria();
            
            if (categoriaNew != null) {
                categoriaNew = em.getReference(categoriaNew.getClass(), categoriaNew.getIdCate());
                producto.setCategoria(categoriaNew);
            }
            
            producto = em.merge(producto);
            
            if (categoriaOld != null && !categoriaOld.equals(categoriaNew)) {
                categoriaOld.getProductos().remove(producto);
                categoriaOld = em.merge(categoriaOld);
            }
            if (categoriaNew != null && !categoriaNew.equals(categoriaOld)) {
                categoriaNew.getProductos().add(producto);
                em.merge(categoriaNew);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof NonexistentEntityException) {
                throw (NonexistentEntityException) e;
            }
            throw new RuntimeException("Error al editar el producto", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public void destroy(Integer id) throws NonexistentEntityException, IllegalOrphanException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getIdProd(); // Forzar carga
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException(
                    "El producto con id " + id + " no existe.", enfe
                );
            }
            
            List<String> illegalOrphanMessages = null;
            List<Inventario> inventariosCollectionOrphanCheck = producto.getInventarios();
            if (inventariosCollectionOrphanCheck != null && !inventariosCollectionOrphanCheck.isEmpty()) {
                illegalOrphanMessages = new ArrayList<>();
                for (Inventario inventariosCollectionOrphanCheckInventario : inventariosCollectionOrphanCheck) {
                    illegalOrphanMessages.add(
                        "El producto (" + producto + ") no puede ser eliminado porque tiene el inventario " + 
                        inventariosCollectionOrphanCheckInventario + " asociado."
                    );
                }
            }
            if (illegalOrphanMessages != null && !illegalOrphanMessages.isEmpty()) {
                throw new IllegalOrphanException(String.join("\n", illegalOrphanMessages));
            }
            
            Categoria categoria = producto.getCategoria();
            if (categoria != null) {
                categoria.getProductos().remove(producto);
                em.merge(categoria);
            }
            
            em.remove(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof NonexistentEntityException) {
                throw (NonexistentEntityException) e;
            }
            if (e instanceof IllegalOrphanException) {
                throw (IllegalOrphanException) e;
            }
            throw new RuntimeException("Error al eliminar el producto", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public Producto findProducto(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }
    
    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }
    
    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Producto> cq = em.getCriteriaBuilder().createQuery(Producto.class);
            cq.select(cq.from(Producto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Producto> findProductosByCategoria(Integer idCategoria) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createQuery(
                "SELECT p FROM Producto p WHERE p.categoria.idCate = :idCategoria ORDER BY p.nomProd"
            );
            query.setParameter("idCategoria", idCategoria);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Producto> findProductosByNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createQuery(
                "SELECT p FROM Producto p WHERE LOWER(p.nomProd) LIKE LOWER(:nombre) ORDER BY p.nomProd"
            );
            query.setParameter("nombre", "%" + nombre + "%");
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public int getProductoCount() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            cq.select(em.getCriteriaBuilder().count(cq.from(Producto.class)));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}