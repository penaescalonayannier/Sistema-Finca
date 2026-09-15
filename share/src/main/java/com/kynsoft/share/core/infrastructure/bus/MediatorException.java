package com.kynsoft.share.core.infrastructure.bus;

/**
 * Excepción personalizada para errores relacionados con el Mediator
 */
public class MediatorException extends RuntimeException {
    
    public MediatorException(String message) {
        super(message);
    }

    public MediatorException(String message, Throwable cause) {
        super(message, cause);
    }
}