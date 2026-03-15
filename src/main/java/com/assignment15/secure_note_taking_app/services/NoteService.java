package com.assignment15.secure_note_taking_app.services;

import com.assignment15.secure_note_taking_app.model.Note;
import com.assignment15.secure_note_taking_app.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;
    
    public Note createNote(Note note, String username) {
        note.setOwnerUsername(username);
        return noteRepository.save(note);
    }
    
    public List<Note> getMyNotes(String username) {
        return noteRepository.findByOwnerUsername(username);
    }
    
    public Optional<Note> getMyNoteById(Long id, String username) {
        return noteRepository.findByIdAndOwnerUsername(id, username);
    }
    
    public void deleteMyNote(Long id, String username) {
        Note note = noteRepository.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Note not found or Access Denied"));
        noteRepository.delete(note);
    }
    
    // Admin methods
    public List<Note> getAllNotesForAdmin() { return noteRepository.findAll(); }
    public void deleteNoteAdmin(Long id) { noteRepository.deleteById(id); }
}
