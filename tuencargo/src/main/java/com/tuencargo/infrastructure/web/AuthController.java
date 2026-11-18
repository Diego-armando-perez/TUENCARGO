package com.tuencargo.infrastructure.web;

import com.tuencargo.application.ports.in.AuthUseCase;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthUseCase auth;

    public AuthController(AuthUseCase auth) {
        this.auth = auth;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String registered,
                        @RequestParam(required = false) String logout,
                        Authentication authentication,
                        Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return "redirect:/";
        }
        model.addAttribute("loginError", error != null);
        model.addAttribute("registered", registered != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }

    @GetMapping("/register")
    public String form(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam(required = false) String fullName,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String confirmPassword,
                           Model model) {
        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        if (password == null || !password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }
        try {
            auth.register(fullName, email, password);
            return "redirect:/login?registered";
        } catch (DomainException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }
}
