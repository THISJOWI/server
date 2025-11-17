// ...existing code...
package uk.thisjowi.Notes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.thisjowi.Notes.Utils.EncryptionUtil;
import uk.thisjowi.Notes.entity.Note;
import uk.thisjowi.Notes.repository.NoteRepository;

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
        notes.forEach(note -> note.setContent(EncryptionUtil.decrypt(note.getContent())));
        return notes;
    }

    // Search notes by title fragment (without filtering by user)
    @Transactional(readOnly = true)
    public List<Note> searchNotesByTitle(String title) {
        if (title == null) title = "";
        List<Note> notes = noteRepository.findByTitleIgnoreCaseContaining(title);
        notes.forEach(n -> n.setContent(EncryptionUtil.decrypt(n.getContent())));
        return notes;
    }

    @Transactional
    public Note saveNote(Note note) {
        note.setContent(EncryptionUtil.encrypt(note.getContent()));
        Note saved = noteRepository.save(note);
        // Return with decrypted content (DB has the encrypted version)
        saved.setContent(EncryptionUtil.decrypt(saved.getContent()));
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Note> getNoteByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Cannot search for a blank note");
        }
        return noteRepository.findByTitleIgnoreCase(title)
                .map(note -> {
                    note.setContent(EncryptionUtil.decrypt(note.getContent()));
                    return note;
                });
    }

    public Optional<Note> getNoteByCretedAt(LocalDateTime createdAt) {
        return noteRepository.findByCreatedAt(createdAt);
    }

    @Transactional(readOnly = true)
    public List<Note> getNotesByUserId(Long userId) {
        List<Note> notes = noteRepository.findByUserId(userId);
        notes.forEach(note -> note.setContent(EncryptionUtil.decrypt(note.getContent())));
        return notes;
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
        // Encrypt content before updating
        if (note.getContent() != null) {
            note.setContent(EncryptionUtil.encrypt(note.getContent()));
        }
        Note saved = noteRepository.save(note);
        // Return with decrypted content
        if (saved.getContent() != null) {
            saved.setContent(EncryptionUtil.decrypt(saved.getContent()));
        }
        return saved;
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
            // Update allowed fields
            if (noteDetails.getTitle() != null && !noteDetails.getTitle().isBlank()) {
                noteToUpdate.setTitle(noteDetails.getTitle());
            }
            if (noteDetails.getContent() != null) {
                // Encrypt before saving
                noteToUpdate.setContent(EncryptionUtil.encrypt(noteDetails.getContent()));
            }
            Note saved = noteRepository.save(noteToUpdate);
            // Return with decrypted content
            saved.setContent(EncryptionUtil.decrypt(saved.getContent()));
            return Optional.of(saved);
        }
        return Optional.empty();
    }

    // New: Search notes that contain the title (case-insensitive) and return with decrypted content
    @Transactional(readOnly = true)
    public List<Note> searchNotesByTitleAndUserId(String title, Long userId) {
        if (title == null) title = "";
        List<Note> notes = noteRepository.findByTitleIgnoreCaseContainingAndUserId(title, userId);
        notes.forEach(n -> n.setContent(EncryptionUtil.decrypt(n.getContent())));
        return notes;
    }

    @Transactional(readOnly = true)
    public Optional<Note> getNoteByTitleAndUserId(String title, Long userId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Cannot search for a blank note");
        }
        return noteRepository.findByTitleIgnoreCaseAndUserId(title, userId)
                .map(note -> {
                    note.setContent(EncryptionUtil.decrypt(note.getContent()));
                    return note;
                });
    }

    @Transactional
    public Optional<Note> updateNoteByTitleAndUserId(String title, Note noteDetails, Long userId) {
        if (title == null || title.isBlank()) return Optional.empty();
        Optional<Note> existingOpt = noteRepository.findByTitleIgnoreCaseAndUserId(title, userId);
        if (existingOpt.isPresent()) {
            Note noteToUpdate = existingOpt.get();
            // Update allowed fields
            if (noteDetails.getTitle() != null && !noteDetails.getTitle().isBlank()) {
                noteToUpdate.setTitle(noteDetails.getTitle());
            }
            if (noteDetails.getContent() != null) {
                // Encrypt before saving
                noteToUpdate.setContent(EncryptionUtil.encrypt(noteDetails.getContent()));
            }
            noteToUpdate.setUserId(userId); // Ensure userId remains
            Note saved = noteRepository.save(noteToUpdate);
            // Return with decrypted content
            saved.setContent(EncryptionUtil.decrypt(saved.getContent()));
            return Optional.of(saved);
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
}
