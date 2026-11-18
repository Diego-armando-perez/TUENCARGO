package com.tuencargo.infrastructure.adapter.fx;

import com.tuencargo.application.ports.out.ExchangeRatePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adaptador simulado de la TRM. El valor se puede cambiar en application.properties
 * (tuencargo.fx.usd-cop) sin tocar el dominio.
 */
@Component
public class MockExchangeRateAdapter implements ExchangeRatePort {

    private final BigDecimal usdToCop;

    public MockExchangeRateAdapter(@Value("${tuencargo.fx.usd-cop:4100.00}") BigDecimal usdToCop) {
        this.usdToCop = usdToCop;
    }

    @Override
    public BigDecimal usdToCop() {
        return usdToCop;
    }
}
