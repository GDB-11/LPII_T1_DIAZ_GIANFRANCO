package cibertec.com.diaz.badoino.controller.exceptions;

/**
 * Excepción lanzada cuando no se encuentra una entidad
 */
public class NonexistentEntityException extends Exception {
    
    public NonexistentEntityException(String message) {
        super(message);
    }
    
    public NonexistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}