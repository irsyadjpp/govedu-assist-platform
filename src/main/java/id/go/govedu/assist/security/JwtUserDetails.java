package id.go.govedu.assist.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class JwtUserDetails implements UserDetails {

    private final String subject;
    private final UUID userId;
    private final String role;
    private final String instCode;
    private final String userType; // "USER" or "ADMIN"

    public JwtUserDetails(String subject, UUID userId, String role, String instCode, String userType) {
        this.subject = subject;
        this.userId = userId;
        this.role = role;
        this.instCode = instCode;
        this.userType = userType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return subject;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getInstCode() {
        return instCode;
    }

    public String getUserType() {
        return userType;
    }
}
