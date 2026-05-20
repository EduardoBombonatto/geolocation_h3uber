package com.eduardo.geolocation_h3uber.entities;

public enum UserRole {
    CLIENT("ROLE_CLIENT"),
    COMPANY("ROLE_COMPANY");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}