package Javabot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(chain = true)
@AllArgsConstructor
@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private boolean expired;
    private boolean locked;
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JokeHistory> jokeHistories;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserRole> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream().map(UserRole::getUserAuthority).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !expired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}