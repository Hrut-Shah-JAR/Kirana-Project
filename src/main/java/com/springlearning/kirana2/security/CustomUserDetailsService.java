package com.springlearning.kirana2.security;

import com.springlearning.kirana2.entity.User;
import com.springlearning.kirana2.repository.UserRepository;
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

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String authorityName = "ROLE_" + user.getRole().name().toUpperCase();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authorityName));

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPasswordHash(),
                authorities
        );
    }
}
