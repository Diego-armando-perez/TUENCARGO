package com.tuencargo.domain.model;

public enum QuotationStatus {
    VIGENTE,
    CONSUMIDA,
    VENCIDA;

    public String label() {
        return switch (this) {
            case VIGENTE -> "Vigente";
            case CONSUMIDA -> "Usada en un pedido";
            case VENCIDA -> "Vencida";
        };
    }
}
