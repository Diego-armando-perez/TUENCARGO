package com.tuencargo.infrastructure.web;

import com.tuencargo.application.ports.in.OrderUseCase;
import com.tuencargo.application.ports.in.PaymentUseCase;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.exception.NotFoundException;
import com.tuencargo.domain.model.Order;
import com.tuencargo.domain.model.OrderStatus;
import com.tuencargo.domain.model.PaymentMethod;
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

import java.util.Locale;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderUseCase orders;
    private final PaymentUseCase payments;

    public OrderController(OrderUseCase orders, PaymentUseCase payments) {
        this.orders = orders;
        this.payments = payments;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        model.addAttribute("orders", orders.listByUser(user.id()));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal AuthenticatedUser user) {
        Order order = orders.get(id);
        requireAccess(order, user);
        model.addAttribute("order", order);
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("canPay", order.status() == OrderStatus.PENDIENTE_PAGO);
        model.addAttribute("canCancel", order.status() == OrderStatus.PENDIENTE_PAGO
                || order.status() == OrderStatus.COMPRADO);
        model.addAttribute("nextStatus", order.status().nextLogisticStatus());
        return "orders/detail";
    }

    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id,
                      @RequestParam(defaultValue = "PSE") String method,
                      @RequestParam(required = false) String document,
                      @AuthenticationPrincipal AuthenticatedUser user,
                      RedirectAttributes flash) {
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(method.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("error", "El medio de pago seleccionado no es válido");
            return "redirect:/orders/" + id;
        }
        try {
            PaymentUseCase.PaymentOutcome outcome = payments.pay(id, user.id(), paymentMethod, document);
            if (outcome.approved()) {
                flash.addFlashAttribute("ok", "Pago aprobado. Referencia " + outcome.reference());
            } else {
                flash.addFlashAttribute("error", "Pago rechazado: " + outcome.message());
            }
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser user,
                         RedirectAttributes flash) {
        try {
            orders.cancel(id, user.id());
            flash.addFlashAttribute("ok", "Pedido cancelado");
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    private void requireAccess(Order order, AuthenticatedUser user) {
        if (!user.isAdmin() && !order.userId().equals(user.id())) {
            throw new NotFoundException("El pedido no existe");
        }
    }
}
