package cibertec.com.diaz.badoino.controller;

import cibertec.com.diaz.badoino.controller.exceptions.NonexistentEntityException;
import cibertec.com.diaz.badoino.model.Inventario;
import cibertec.com.diaz.badoino.model.Producto;
import cibertec.com.diaz.badoino.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador JPA para la entidad Inventario
 */
public class InventarioJpaController {
    
    public void create(Inventario inventario) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            if (inventario.getFecha() == null) {
                inventario.setFecha(LocalDateTime.now());
            }
            
            Producto producto = inventario.getProducto();
            if (producto != null) {
                producto = em.getReference(producto.getClass(), producto.getIdProd());
                inventario.setProducto(producto);
            }
            
            em.persist(inventario);
            
            if (producto != null) {
                producto.getInventarios().add(inventario);
                em.merge(producto);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al crear el registro de inventario", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public void edit(Inventario inventario) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Inventario persistentInventario = em.find(Inventario.class, inventario.getNroInventario());
            if (persistentInventario == null) {
                throw new NonexistentEntityException(
                    "El inventario con id " + inventario.getNroInventario() + " no existe."
                );
            }
            
            Producto productoOld = persistentInventario.getProducto();
            Producto productoNew = inventario.getProducto();
            
            if (productoNew != null) {
                productoNew = em.getReference(productoNew.getClass(), productoNew.getIdProd());
                inventario.setProducto(productoNew);
            }
            
            inventario = em.merge(inventario);
            
            if (productoOld != null && !productoOld.equals(productoNew)) {
                productoOld.getInventarios().remove(inventario);
                productoOld = em.merge(productoOld);
            }
            if (productoNew != null && !productoNew.equals(productoOld)) {
                productoNew.getInventarios().add(inventario);
                em.merge(productoNew);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof NonexistentEntityException) {
                throw (NonexistentEntityException) e;
            }
            throw new RuntimeException("Error al editar el registro de inventario", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Inventario inventario;
            try {
                inventario = em.getReference(Inventario.class, id);
                inventario.getNroInventario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException(
                    "El inventario con id " + id + " no existe.", enfe
                );
            }
            
            Producto producto = inventario.getProducto();
            if (producto != null) {
                producto.getInventarios().remove(inventario);
                em.merge(producto);
            }
            
            em.remove(inventario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof NonexistentEntityException) {
                throw (NonexistentEntityException) e;
            }
            throw new RuntimeException("Error al eliminar el registro de inventario", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public Inventario findInventario(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Inventario.class, id);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Inventario> findInventarioEntities() {
        return findInventarioEntities(true, -1, -1);
    }
    
    public List<Inventario> findInventarioEntities(int maxResults, int firstResult) {
        return findInventarioEntities(false, maxResults, firstResult);
    }
    
    private List<Inventario> findInventarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Inventario> cq = em.getCriteriaBuilder().createQuery(Inventario.class);
            cq.select(cq.from(Inventario.class));
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
    
    public List<Inventario> findInventariosByProducto(Integer idProducto) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createQuery(
                "SELECT i FROM Inventario i WHERE i.producto.idProd = :idProducto ORDER BY i.fecha DESC"
            );
            query.setParameter("idProducto", idProducto);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Inventario> findInventariosByFechaRange(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createQuery(
                "SELECT i FROM Inventario i WHERE i.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY i.fecha DESC"
            );
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Inventario> findUltimosInventarios(int cantidad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Query query = em.createQuery(
                "SELECT i FROM Inventario i ORDER BY i.fecha DESC"
            );
            query.setMaxResults(cantidad);
            return query.getResultList();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public int getInventarioCount() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            cq.select(em.getCriteriaBuilder().count(cq.from(Inventario.class)));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}