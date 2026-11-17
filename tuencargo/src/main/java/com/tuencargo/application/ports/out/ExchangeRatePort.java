package com.tuencargo.application.ports.out;

import java.math.BigDecimal;

/** Puerto de salida hacia la TRM oficial USD/COP. */
public interface ExchangeRatePort {

    BigDecimal usdToCop();
}
