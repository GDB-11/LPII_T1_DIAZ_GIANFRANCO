package cibertec.com.diaz.badoino.controller.exceptions;

/**
 * Excepción lanzada cuando hay una violación de integridad referencial
 */
public class IllegalOrphanException extends Exception {
    
    public IllegalOrphanException(String message) {
        super(message);
    }
    
    public IllegalOrphanException(String message, Throwable cause) {
        super(message, cause);
    }
}