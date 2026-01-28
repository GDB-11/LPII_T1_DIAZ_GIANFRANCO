package cibertec.com.diaz.badoino.controller;

import cibertec.com.diaz.badoino.controller.exceptions.IllegalOrphanException;
import cibertec.com.diaz.badoino.controller.exceptions.NonexistentEntityException;
import cibertec.com.diaz.badoino.model.Categoria;
import cibertec.com.diaz.badoino.model.Producto;
import cibertec.com.diaz.badoino.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador JPA para la entidad Categoria
 */
public class CategoriaJpaController {
    
    public void create(Categoria categoria) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            if (categoria.getProductos() == null) {
                categoria.setProductos(new ArrayList<>());
            }
            
            em.persist(categoria);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error al crear la categoría", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public void edit(Categoria categoria) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getIdCate());
            if (persistentCategoria == null) {
                throw new NonexistentEntityException(
                    "La categoría con id " + categoria.getIdCate() + " no existe."
                );
            }
            
            categoria = em.merge(categoria);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof NonexistentEntityException) {
                throw (NonexistentEntityException) e;
            }
            throw new RuntimeException("Error al editar la categoría", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public void destroy(Integer id) throws NonexistentEntityException, IllegalOrphanException {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getIdCate();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException(
                    "La categoría con id " + id + " no existe.", enfe
                );
            }
            
            List<String> illegalOrphanMessages = null;
            List<Producto> productosCollectionOrphanCheck = categoria.getProductos();
            if (productosCollectionOrphanCheck != null && !productosCollectionOrphanCheck.isEmpty()) {
                illegalOrphanMessages = new ArrayList<>();
                for (Producto productosCollectionOrphanCheckProducto : productosCollectionOrphanCheck) {
                    illegalOrphanMessages.add(
                        "La categoría (" + categoria + ") no puede ser eliminada porque tiene el producto " + 
                        productosCollectionOrphanCheckProducto + " asociado."
                    );
                }
            }
            if (illegalOrphanMessages != null && !illegalOrphanMessages.isEmpty()) {
                throw new IllegalOrphanException(String.join("\n", illegalOrphanMessages));
            }
            
            em.remove(categoria);
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
            throw new RuntimeException("Error al eliminar la categoría", e);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public Categoria findCategoria(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }
    
    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }
    
    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Categoria> cq = em.getCriteriaBuilder().createQuery(Categoria.class);
            cq.select(cq.from(Categoria.class));
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
    
    public int getCategoriaCount() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
            cq.select(em.getCriteriaBuilder().count(cq.from(Categoria.class)));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}