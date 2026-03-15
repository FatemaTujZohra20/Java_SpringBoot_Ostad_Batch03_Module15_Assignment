package com.assignment15.secure_note_taking_app.services;

import com.assignment15.secure_note_taking_app.model.UserEntity;
import com.assignment15.secure_note_taking_app.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    
    // Using Constructor Injection (Better practice!)
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch user from DB
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // 2. Map our UserEntity to Spring Security's UserDetails
        // Note: .roles() automatically adds the "ROLE_" prefix.
        // If your DB already has "ROLE_USER", use .authorities("ROLE_USER") instead.
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // This must be a BCrypt hash
                .roles(user.getRole().replace("ROLE_", ""))
                .build();
    }
}
