package com.assignment15.secure_note_taking_app.controllers;

import com.assignment15.secure_note_taking_app.model.UserEntity;
import com.assignment15.secure_note_taking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/user")
    public String registerUser (@RequestBody UserEntity user) {
        // 1. Check if user already exists (Optional but good)
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Error: Username is already taken!";
        }
        
        // 2. ENCODE the raw password from the request
        String secret = passwordEncoder.encode(user.getPassword());
        user.setPassword(secret);
        
        // 3. SET THE ROLE (Requirement: ROLE_USER)
        user.setRole("ROLE_USER");
        
        // 4. SAVE to Database
        userRepository.save(user);
        
        return "User registered successfully with ROLE_USER!";
    }
    
    @PostMapping("/admin")
    public String registerAdmin (@RequestBody UserEntity user) {
        // 1. Check if user already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Error: Username is already taken!";
        }
        
        // 2. ENCODE the raw password
        // This is the step that fixes the "401 Unauthorized" error
        String secret = passwordEncoder.encode(user.getPassword());
        user.setPassword(secret);
        
        // 3. SET THE ROLE (Requirement: ROLE_ADMIN)
        // Spring Security expects roles to start with "ROLE_" by default
        user.setRole("ROLE_ADMIN");
        
        // 4. SAVE to Database
        userRepository.save(user);
        
        return "Admin registered successfully with ROLE_ADMIN!";
    }
}
