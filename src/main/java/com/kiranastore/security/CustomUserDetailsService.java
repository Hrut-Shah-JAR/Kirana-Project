package com.kiranastore.security;

import com.kiranastore.entity.User;
import com.kiranastore.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Creates the user details service.
     *
     * @param userRepository repository for user lookup
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by user id for authentication.
     *
     * @param userId user identifier
     * @return user details with authorities
     * @throws UsernameNotFoundException when user cannot be found
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        String authorityName = "ROLE_" + user.getRole().name().toUpperCase();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authorityName));

        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPasswordHash(),
                authorities
        );
    }
}
