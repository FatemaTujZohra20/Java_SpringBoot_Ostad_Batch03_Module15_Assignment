package com.assignment15.secure_note_taking_app.controllers;

import com.assignment15.secure_note_taking_app.model.Note;
import com.assignment15.secure_note_taking_app.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;
    
    @PostMapping
    public Note create (@RequestBody Note note, @AuthenticationPrincipal UserDetails user) {
        return noteService.createNote(note, user.getUsername());
    }
    
    @GetMapping
    public List<Note> getAll (@AuthenticationPrincipal UserDetails user) {
        return noteService.getMyNotes(user.getUsername());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById (@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return noteService.getMyNoteById(id, user.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
