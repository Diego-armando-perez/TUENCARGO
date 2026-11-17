package com.tuencargo.infrastructure.web;

import com.tuencargo.application.ports.in.OrderUseCase;
import com.tuencargo.application.ports.in.QuotationUseCase;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.Order;
import com.tuencargo.domain.model.Quotation;
import com.tuencargo.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Clock;
import java.util.List;

@Controller
@RequestMapping("/quotations")
public class QuotationController {

    private final QuotationUseCase quotations;
    private final OrderUseCase orders;
    private final Clock clock;

    public QuotationController(QuotationUseCase quotations, OrderUseCase orders, Clock clock) {
        this.quotations = quotations;
        this.orders = orders;
        this.clock = clock;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        model.addAttribute("quotations", quotations.listByUser(user.id()));
        return "quotations/list";
    }

    @GetMapping("/new")
    public String form() {
        return "quotations/form";
    }

    @PostMapping
    public String create(@RequestParam(required = false) String url,
                         @AuthenticationPrincipal AuthenticatedUser user,
                         Model model,
                         RedirectAttributes flash) {
        try {
            Quotation quotation = quotations.create(user.id(), url);
            flash.addFlashAttribute("ok", "Cotización generada correctamente");
            return "redirect:/quotations/" + quotation.id();
        } catch (DomainException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("url", url);
            return "quotations/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal AuthenticatedUser user) {
        Quotation quotation = quotations.get(id);
        requireAccess(quotation, user);
        model.addAttribute("quotation", quotation);
        model.addAttribute("expired", quotation.isExpired(clock.instant()));
        return "quotations/detail";
    }

    @GetMapping("/{id}/compare")
    public String compare(@PathVariable Long id, Model model,
                          @AuthenticationPrincipal AuthenticatedUser user) {
        Quotation quotation = quotations.get(id);
        requireAccess(quotation, user);
        List<?> options = quotations.compare(id);
        model.addAttribute("quotation", quotation);
        model.addAttribute("options", options);
        model.addAttribute("hasAlternatives", options.size() > 1);
        return "quotations/compare";
    }

    @PostMapping("/{id}/orders")
    public String checkout(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user,
                           RedirectAttributes flash) {
        try {
            Order order = orders.createFromQuotation(id, user.id());
            flash.addFlashAttribute("ok", "Pedido " + order.orderNumber() + " creado");
            return "redirect:/orders/" + order.id();
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/quotations/" + id;
        }
    }

    private void requireAccess(Quotation quotation, AuthenticatedUser user) {
        if (!user.isAdmin() && !quotation.userId().equals(user.id())) {
            throw new NotFoundException("La cotización no existe");
        }
    }
}
