package com.abhi.auth_service.enums;

public enum RoleName {
    ADMIN("admin"),
    USER("user");

    private final String value;

    private RoleName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
