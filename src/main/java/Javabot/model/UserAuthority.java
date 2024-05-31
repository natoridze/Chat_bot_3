package Javabot.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserAuthority implements GrantedAuthority {
    Place_Orders,
    Manage_Orders,
    FULL;
    @Override
    public String getAuthority() {
        return this.name();
    }
}
