package com.thisjowi.note.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.thisjowi.note.Utils.EncryptionUtil;
import com.thisjowi.note.entity.Note;
import com.thisjowi.note.repository.NoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // Get all notes (without filtering by user)
    @Transactional(readOnly = true)
    public List<Note> getAllNotes() {
        List<Note> notes = noteRepository.findAll();
        return notes.stream().map(this::decryptNote).toList();
    }

    // Search notes by title fragment (without filtering by user)
    @Transactional(readOnly = true)
    public List<Note> searchNotesByTitle(String title) {
        if (title == null) title = "";
        List<Note> notes = noteRepository.findByTitleIgnoreCaseContaining(title);
        return notes.stream().map(this::decryptNote).toList();
    }

    @Transactional
    public Note saveNote(Note note) {
        note.setTitle(EncryptionUtil.encrypt(note.getTitle()));
        note.setContent(EncryptionUtil.encrypt(note.getContent()));
        Note saved = noteRepository.save(note);
        
        // Return a copy with decrypted content to avoid dirty checking update
        Note response = new Note();
        response.setId(saved.getId());
        response.setUserId(saved.getUserId());
        response.setCreatedAt(saved.getCreatedAt());
        response.setTitle(EncryptionUtil.decrypt(saved.getTitle()));
        response.setContent(EncryptionUtil.decrypt(saved.getContent()));
        return response;
    }

    @Transactional(readOnly = true)
    public Optional<Note> getNoteByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Cannot search for a blank note");
        }
        return noteRepository.findByTitleIgnoreCase(title)
                .map(this::decryptNote);
    }

    public Optional<Note> getNoteByCretedAt(LocalDateTime createdAt) {
        return noteRepository.findByCreatedAt(createdAt);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesByUserId(Long userId) {
        List<Note> notes = noteRepository.findByUserId(userId);
        return notes.stream().map(this::decryptNote).toList();
    }

    // Returns true if the note existed and was deleted, false if it didn't exist
    @Transactional
    public boolean deleteNoteById(Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Note updateNote(Note note) {
        // Encrypt title and content before updating
        if (note.getTitle() != null) {
            note.setTitle(EncryptionUtil.encrypt(note.getTitle()));
        }
        if (note.getContent() != null) {
            note.setContent(EncryptionUtil.encrypt(note.getContent()));
        }
        Note saved = noteRepository.save(note);
        
        // Return a copy with decrypted content
        Note response = new Note();
        response.setId(saved.getId());
        response.setUserId(saved.getUserId());
        response.setCreatedAt(saved.getCreatedAt());
        
        if (saved.getTitle() != null) {
            response.setTitle(EncryptionUtil.decrypt(saved.getTitle()));
        }
        if (saved.getContent() != null) {
            response.setContent(EncryptionUtil.decrypt(saved.getContent()));
        }
        return response;
    }

    // New: Delete a note by its title (case-insensitive). Returns true if deleted.
    @Transactional
    public boolean deleteNoteByTitle(String title) {
        if (title == null || title.isBlank()) return false;
        Optional<Note> existing = noteRepository.findByTitleIgnoreCase(title);
        if (existing.isPresent()) {
            noteRepository.delete(existing.get());
            return true;
        }
        return false;
    }

    // New: Update a note found by title (case-insensitive). Returns the updated note with decrypted content.
    @Transactional
    public Optional<Note> updateNoteByTitle(String title, Note noteDetails) {
        if (title == null || title.isBlank()) return Optional.empty();
        Optional<Note> existingOpt = noteRepository.findByTitleIgnoreCase(title);
        if (existingOpt.isPresent()) {
            Note noteToUpdate = existingOpt.get();
            // Update allowed fields (encrypt before saving)
            if (noteDetails.getTitle() != null && !noteDetails.getTitle().isBlank()) {
                noteToUpdate.setTitle(EncryptionUtil.encrypt(noteDetails.getTitle()));
            }
            if (noteDetails.getContent() != null) {
                noteToUpdate.setContent(EncryptionUtil.encrypt(noteDetails.getContent()));
            }
            Note saved = noteRepository.save(noteToUpdate);
            
            // Return a copy with decrypted content
            Note response = new Note();
            response.setId(saved.getId());
            response.setUserId(saved.getUserId());
            response.setCreatedAt(saved.getCreatedAt());
            response.setTitle(EncryptionUtil.decrypt(saved.getTitle()));
            response.setContent(EncryptionUtil.decrypt(saved.getContent()));
            
            return Optional.of(response);
        }
        return Optional.empty();
    }

    // New: Search notes that contain the title (case-insensitive) and return with decrypted content
    @Transactional(readOnly = true)
    public List<Note> searchNotesByTitleAndUserId(String title, Long userId) {
        if (title == null) title = "";
        List<Note> notes = noteRepository.findByTitleIgnoreCaseContainingAndUserId(title, userId);
        return notes.stream().map(this::decryptNote).toList();
    }

    @Transactional(readOnly = true)
    public Optional<Note> getNoteByTitleAndUserId(String title, Long userId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Cannot search for a blank note");
        }
        return noteRepository.findByTitleIgnoreCaseAndUserId(title, userId)
                .map(this::decryptNote);
    }

    @Transactional
    public Optional<Note> updateNoteByTitleAndUserId(String title, Note noteDetails, Long userId) {
        if (title == null || title.isBlank()) return Optional.empty();
        Optional<Note> existingOpt = noteRepository.findByTitleIgnoreCaseAndUserId(title, userId);
        if (existingOpt.isPresent()) {
            Note noteToUpdate = existingOpt.get();
            // Update allowed fields (encrypt before saving)
            if (noteDetails.getTitle() != null && !noteDetails.getTitle().isBlank()) {
                noteToUpdate.setTitle(EncryptionUtil.encrypt(noteDetails.getTitle()));
            }
            if (noteDetails.getContent() != null) {
                noteToUpdate.setContent(EncryptionUtil.encrypt(noteDetails.getContent()));
            }
            noteToUpdate.setUserId(userId); // Ensure userId remains
            Note saved = noteRepository.save(noteToUpdate);
            
            // Return a copy with decrypted title and content
            Note response = new Note();
            response.setId(saved.getId());
            response.setUserId(saved.getUserId());
            response.setCreatedAt(saved.getCreatedAt());
            response.setTitle(EncryptionUtil.decrypt(saved.getTitle()));
            response.setContent(EncryptionUtil.decrypt(saved.getContent()));
            
            return Optional.of(response);
        }
        return Optional.empty();
    }

    @Transactional
    public boolean deleteNoteByTitleAndUserId(String title, Long userId) {
        if (title == null || title.isBlank()) return false;
        Optional<Note> existing = noteRepository.findByTitleIgnoreCaseAndUserId(title, userId);
        if (existing.isPresent()) {
            noteRepository.delete(existing.get());
            return true;
        }
        return false;
    }

    private Note decryptNote(Note note) {
        Note copy = new Note();
        copy.setId(note.getId());
        copy.setUserId(note.getUserId());
        copy.setCreatedAt(note.getCreatedAt());
        copy.setTitle(EncryptionUtil.decrypt(note.getTitle()));
        copy.setContent(EncryptionUtil.decrypt(note.getContent()));
        return copy;
    }
}
