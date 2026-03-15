package com.assignment15.secure_note_taking_app.repository;

import com.assignment15.secure_note_taking_app.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByOwnerUsername(String username);
    Optional<Note> findByIdAndOwnerUsername(Long id, String username);
}
