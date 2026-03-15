package com.assignment15.secure_note_taking_app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role; // it should be stored as "ROLE_USER" or "ROLE_ADMIN"
}
