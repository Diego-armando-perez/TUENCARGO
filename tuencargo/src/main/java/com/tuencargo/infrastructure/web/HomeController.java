package com.tuencargo.infrastructure.web;

import com.tuencargo.application.ports.in.AuthUseCase;
import com.tuencargo.application.ports.in.NotificationUseCase;
import com.tuencargo.application.ports.in.OrderUseCase;
import com.tuencargo.application.ports.in.QuotationUseCase;
import com.tuencargo.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final QuotationUseCase quotations;
    private final OrderUseCase orders;
    private final NotificationUseCase notifications;
    private final AuthUseCase auth;

    public HomeController(QuotationUseCase quotations, OrderUseCase orders,
                          NotificationUseCase notifications, AuthUseCase auth) {
        this.quotations = quotations;
        this.orders = orders;
        this.notifications = notifications;
        this.auth = auth;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        Object principal = authentication == null ? null : authentication.getPrincipal();
        if (!(principal instanceof AuthenticatedUser user)) {
            model.addAttribute("anonymous", true);
            return "index";
        }
        model.addAttribute("anonymous", false);
        model.addAttribute("account", auth.getByEmail(user.email()));
        model.addAttribute("quotations", quotations.listByUser(user.id()).stream().limit(5).toList());
        model.addAttribute("orders", orders.listByUser(user.id()).stream().limit(5).toList());
        model.addAttribute("notifications", notifications.listByUser(user.id()).stream().limit(3).toList());
        return "index";
    }
}
