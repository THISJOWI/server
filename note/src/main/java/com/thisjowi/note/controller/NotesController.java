package uk.thisjowi.Notes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.thisjowi.Notes.entity.Note;
import uk.thisjowi.Notes.repository.NoteRepository;
import uk.thisjowi.Notes.service.NoteService;
import uk.thisjowi.Notes.service.AuthenticationClient;
import uk.thisjowi.Notes.kafka.KafkaConsumerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

    @Autowired
    private NoteService notesService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private AuthenticationClient authenticationClient;

    /**
     * Extracts the userId from the JWT token in the Authorization header
     * @param authHeader the value of the Authorization header (ex: "Bearer token...")
     * @return the userId of the authenticated user, or null if invalid
     */
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }
        Long userId = authenticationClient.getUserIdFromToken(authHeader);
        // getUserIdFromToken returns -1 if there is an error
        return (userId != null && userId >= 0) ? userId : null;
    }

    @PostMapping
    public ResponseEntity<Note> createNote(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody Note note) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // Assign the userId of the authenticated user to the note
        note.setUserId(userId);
        Note savedNote = notesService.saveNote(note);
        return ResponseEntity.ok(savedNote);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // Return only the notes of the authenticated user
        List<Note> notes = notesService.getNotesByUserId(userId);
        return ResponseEntity.ok(notes);
    }

    // Search for notes by title fragment (filtered by user)
    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestParam(value = "title", required = false) String title) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        List<Note> notes = notesService.searchNotesByTitleAndUserId(title, userId);
        return ResponseEntity.ok(notes);
    }

    // Get a note by exact title (case-insensitive, validating user ownership)
    @GetMapping("/{title}")
    public ResponseEntity<Note> getNoteByTitle(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @PathVariable String title) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        Optional<Note> noteOpt = notesService.getNoteByTitleAndUserId(title, userId);
        return noteOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update note found by title (validating user ownership)
    @PutMapping("/{title}")
    public ResponseEntity<Note> updateNote(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @PathVariable String title,
            @RequestBody Note noteDetails) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        Optional<Note> updated = notesService.updateNoteByTitleAndUserId(title, noteDetails, userId);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete note by ID (validating user ownership)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @PathVariable Long id) {
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // Verify that the note belongs to the authenticated user
        Optional<Note> noteOpt = noteRepository.findById(id);
        if (noteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Note note = noteOpt.get();
        if (!note.getUserId().equals(userId)) {
            // The note does not belong to the authenticated user
            return ResponseEntity.status(403).build(); // Forbidden
        }

        boolean deleted = notesService.deleteNoteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}