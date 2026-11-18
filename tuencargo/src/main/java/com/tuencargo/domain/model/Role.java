package com.tuencargo.domain.model;

public enum Role {
    ADMIN,
    USER;

    public String authority() {
        return "ROLE_" + name();
    }
}
