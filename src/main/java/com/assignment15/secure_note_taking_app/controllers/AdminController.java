package com.assignment15.secure_note_taking_app.controllers;

import com.assignment15.secure_note_taking_app.model.Note;
import com.assignment15.secure_note_taking_app.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notes")
public class AdminController {
    @Autowired
    private NoteService noteService;
    
    @GetMapping
    public List<Note> getAllNotes () {
        return noteService.getAllNotesForAdmin();
    }
    
    @DeleteMapping("/{id}")
    public String deleteNote (@PathVariable Long id) {
        noteService.deleteNoteAdmin(id);
        return "Note deleted successfully by Admin";
    }
}
