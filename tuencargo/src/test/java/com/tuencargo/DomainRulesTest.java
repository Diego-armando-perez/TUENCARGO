package com.tuencargo;

import com.tuencargo.domain.exception.DomainException;
import com.tuencargo.domain.model.MoneyBreakdown;
import com.tuencargo.domain.model.Order;
import com.tuencargo.domain.model.OrderStatus;
import com.tuencargo.domain.model.StoreOffer;
import com.tuencargo.domain.model.WarehouseRate;
import com.tuencargo.domain.service.QuotationCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainRulesTest {

    @Test
    void calculaElCostoTotalPuestoEnColombia() {
        StoreOffer offer = new StoreOffer("Amazon US", "Audífonos", "https://amazon.com/dp/X",
                null, new BigDecimal("100.00"), new BigDecimal("2.0"));
        WarehouseRate rate = new WarehouseRate(1L, "Estándar", new BigDecimal("28000"),
                new BigDecimal("15000"), new BigDecimal("10"), new BigDecimal("19"), true);

        MoneyBreakdown result = QuotationCalculator.calculate(offer, rate, new BigDecimal("4100"));

        assertEquals(new BigDecimal("410000"), result.productCop());
        assertEquals(new BigDecimal("56000"), result.freightCop());
        assertEquals(new BigDecimal("15000"), result.handlingCop());
        assertEquals(new BigDecimal("41000"), result.serviceFeeCop());
        assertEquals(new BigDecimal("91390"), result.taxCop());
        assertEquals(new BigDecimal("613390"), result.totalCop());
    }

    @Test
    void respetaLaMaquinaDeEstadosDelPedido() {
        Order order = crearPedido();
        assertTrue(order.isPayable());

        Order paid = order.changeStatus(OrderStatus.COMPRADO, "Pago aprobado", Instant.now());
        assertEquals(OrderStatus.COMPRADO, paid.status());
        assertEquals(1, paid.events().size());

        assertThrows(DomainException.class,
                () -> order.changeStatus(OrderStatus.ENTREGADO, "Salto inválido", Instant.now()));
    }

    private Order crearPedido() {
        return new Order(1L, "TC-20261116-0001", 1L, "cliente@tuencargo.com", 1L, "Producto",
                "Amazon US", new BigDecimal("500000"), OrderStatus.PENDIENTE_PAGO, Instant.now(),
                Instant.now(), new java.util.ArrayList<>());
    }
}
