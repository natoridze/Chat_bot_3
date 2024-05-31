package Javabot.service;

import Javabot.model.User;
import Javabot.model.UserAuthority;
import Javabot.model.UserRole;
import Javabot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserServiceIMPL implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    @Transactional
    public void registration(String username, String password) {
        // Шифрование пароля
        String encodedPassword = passwordEncoder.encode(password);

        // Создание пользователя
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setExpired(false);
        user.setLocked(false);

        // Создание роли пользователя
        UserRole userRole = new UserRole();
        userRole.setUserAuthority(UserAuthority.FULL);
        userRole.setUser(user);

        // Связывание роли с пользователем
        user.setUserRoles(Collections.singletonList(userRole));

        // Сохранение пользователя в базе данных
        userRepository.save(user);
    }
}