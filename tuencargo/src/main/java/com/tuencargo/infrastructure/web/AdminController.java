package com.tuencargo.infrastructure.web;

import com.tuencargo.application.ports.in.OrderUseCase;
import com.tuencargo.application.ports.in.WarehouseRateUseCase;
import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.model.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int MAX_RATE_NAME_LENGTH = 80;
    private static final int MAX_NOTE_LENGTH = 300;

    private final WarehouseRateUseCase rates;
    private final OrderUseCase orders;

    public AdminController(WarehouseRateUseCase rates, OrderUseCase orders) {
        this.rates = rates;
        this.orders = orders;
    }

    @GetMapping
    public String home(Model model) {
        List<Order> allOrders = orders.listAll();
        model.addAttribute("orderCount", allOrders.size());
        model.addAttribute("rateCount", rates.listAll().size());
        model.addAttribute("recentOrders", allOrders.stream().limit(5).toList());
        return "admin/home";
    }

    @GetMapping("/rates")
    public String rates(Model model) {
        model.addAttribute("rates", rates.listAll());
        return "admin/rates";
    }

    @PostMapping("/rates")
    public String createRate(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String pricePerLb,
                             @RequestParam(required = false) String handlingFee,
                             @RequestParam(required = false) String serviceFeePercent,
                             @RequestParam(required = false) String taxPercent,
                             Model model,
                             RedirectAttributes flash) {
        String cleanName = name == null ? null : name.trim();
        if (cleanName != null && cleanName.length() > MAX_RATE_NAME_LENGTH) {
            flash.addFlashAttribute("error",
                    "El nombre de la tarifa no puede superar " + MAX_RATE_NAME_LENGTH + " caracteres");
            return "redirect:/admin/rates";
        }
        try {
            rates.create(cleanName, decimal(pricePerLb, "precio por libra"),
                    decimal(handlingFee, "tarifa de manejo"),
                    decimal(serviceFeePercent, "porcentaje de servicio"),
                    decimal(taxPercent, "porcentaje de impuestos"));
            flash.addFlashAttribute("ok", "Tarifa creada y activada");
            return "redirect:/admin/rates";
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/rates";
        }
    }

    @PostMapping("/rates/{id}/toggle")
    public String toggleRate(@PathVariable Long id, RedirectAttributes flash) {
        try {
            rates.toggle(id);
            flash.addFlashAttribute("ok", "Estado de la tarifa actualizado");
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/rates";
    }

    @PostMapping("/rates/{id}/delete")
    public String deleteRate(@PathVariable Long id, RedirectAttributes flash) {
        try {
            rates.delete(id);
            flash.addFlashAttribute("ok", "Tarifa eliminada");
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/rates";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orders.listAll());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/advance")
    public String advance(@PathVariable Long id,
                          @RequestParam(required = false) String note,
                          RedirectAttributes flash) {
        String cleanNote = note == null ? null : note.trim();
        if (cleanNote != null && cleanNote.length() > MAX_NOTE_LENGTH) {
            flash.addFlashAttribute("error",
                    "La nota no puede superar " + MAX_NOTE_LENGTH + " caracteres");
            return "redirect:/admin/orders";
        }
        try {
            Order updated = orders.advanceLogisticStatus(id, cleanNote);
            flash.addFlashAttribute("ok", "Pedido " + updated.orderNumber() + " ahora está en '"
                    + updated.status().label() + "'");
        } catch (DomainException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/orders";
    }

    private BigDecimal decimal(String raw, String field) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El " + field + " es obligatorio");
        }
        String value = raw.trim().replace(" ", "");
        int lastComma = value.lastIndexOf(',');
        int lastDot = value.lastIndexOf('.');
        if (lastComma >= 0 && lastDot >= 0) {
            // Estan los dos separadores: el ultimo es el decimal.
            value = lastComma > lastDot
                    ? value.replace(".", "").replace(',', '.')   // 28.000,50
                    : value.replace(",", "");                    // 28,000.50
        } else if (lastComma >= 0) {
            value = value.replace(',', '.');                     // 28000,50
        } else if (lastDot >= 0 && value.indexOf('.') != lastDot) {
            value = value.replace(".", "");                      // 28.000.000
        } else if (lastDot >= 0 && value.substring(lastDot + 1).length() == 3
                && !value.startsWith("0.")) {
            throw new DomainException("El " + field
                    + " es ambiguo. Escribe el valor sin separador de miles, por ejemplo 28000 o 28000,50");
        }
        try {
            BigDecimal parsed = new BigDecimal(value);
            if (parsed.signum() < 0) {
                throw new DomainException("El " + field + " no puede ser negativo");
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new DomainException("El " + field + " debe ser numérico");
        }
    }
}
