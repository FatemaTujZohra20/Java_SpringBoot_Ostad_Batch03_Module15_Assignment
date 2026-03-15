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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "User registered successfully";
    }
    
    @PostMapping("/admin")
    public String registerAdmin (@RequestBody UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);
        return "Admin registered successfully";
    }
}
