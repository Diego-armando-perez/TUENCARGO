package com.tuencargo.domain.exception;

/**
 * Violación de una regla de negocio del dominio.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
