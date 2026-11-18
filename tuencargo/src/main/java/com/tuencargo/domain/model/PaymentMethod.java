package com.tuencargo.domain.model;

public enum PaymentMethod {
    PSE,
    CARD,
    TRANSFER;

    public String label() {
        return switch (this) {
            case PSE -> "PSE (débito bancario)";
            case CARD -> "Tarjeta de crédito/débito";
            case TRANSFER -> "Transferencia bancaria";
        };
    }
}
