package br.com.bradesco.investment.data.model;

import org.springframework.security.core.GrantedAuthority;

public enum AccountType implements GrantedAuthority {

    BASIC_ACCOUNT, PREMIUM_ACCOUNT;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
