package com.tuencargo.infrastructure.web;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilidades de presentación usadas desde las plantillas Thymeleaf como ${@fmt...}.
 */
@Component("fmt")
public class Formats {

    private static final Locale SPANISH_COLOMBIA = Locale.forLanguageTag("es-CO");
    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(BOGOTA);
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(BOGOTA);

    public String money(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        NumberFormat format = NumberFormat.getIntegerInstance(SPANISH_COLOMBIA);
        format.setMaximumFractionDigits(0);
        format.setGroupingUsed(true);
        return format.format(value);
    }

    public String dt(Instant value) {
        return value == null ? "" : DATE_TIME.format(value);
    }

    public String date(Instant value) {
        return value == null ? "" : DATE.format(value);
    }
}
