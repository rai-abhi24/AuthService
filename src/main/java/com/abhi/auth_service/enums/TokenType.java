package com.abhi.auth_service.enums;

public enum TokenType {
    REFRESH_TOKEN("refresh_token"),
    EMAIL_VERIFICATION_TOKEN("email_verification_token");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
