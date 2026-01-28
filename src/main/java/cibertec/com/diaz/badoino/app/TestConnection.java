package cibertec.com.diaz.badoino.app;

import cibertec.com.diaz.badoino.util.JPAUtil;
import javax.persistence.EntityManager;

/**
 * Clase principal para probar la conexión JPA
 */
public class TestConnection {
    
    public static void main(String[] args) {
        EntityManager em = null;
        
        try {
            // Obtener EntityManager
            em = JPAUtil.getEntityManager();
            
            System.out.println("==============================================");
            System.out.println("✓ Conexión a la base de datos establecida");
            System.out.println("✓ JPA configurado correctamente");
            System.out.println("==============================================");
            
        } catch (Exception e) {
            System.err.println("==============================================");
            System.err.println("✗ Error al conectar con la base de datos");
            System.err.println("==============================================");
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            JPAUtil.closeEntityManager(em);
            JPAUtil.closeEntityManagerFactory();
        }
    }
}