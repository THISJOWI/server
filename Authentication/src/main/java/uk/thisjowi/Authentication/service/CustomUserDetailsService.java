package uk.thisjowi.Authentication.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import uk.thisjowi.Authentication.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            // try email
            optional = userRepository.findByEmail(username);
        }
        var appUser = optional.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User.withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
