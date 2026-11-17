package com.tuencargo.infrastructure.web;

import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.infrastructure.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Datos comunes para todas las vistas (usuario autenticado y rol) y manejo de errores de dominio.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void addCommonData(Model model, Authentication authentication) {
        Object principal = authentication == null ? null : authentication.getPrincipal();
        AuthenticatedUser current = principal instanceof AuthenticatedUser user ? user : null;
        model.addAttribute("cu", current);
        model.addAttribute("isAdmin", current != null && current.isAdmin());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("title", "No encontrado");
        model.addAttribute("message", ex.getMessage());
        return "error/message";
    }

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDomain(DomainException ex, Model model) {
        model.addAttribute("title", "No pudimos completar la operación");
        model.addAttribute("message", ex.getMessage());
        return "error/message";
    }
}
