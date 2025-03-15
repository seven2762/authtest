package com.sparta.homework.domain.model;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum UserRole {

    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");


    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(value);
    }
}
