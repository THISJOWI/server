package uk.thisjowi.Notes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.thisjowi.Notes.entity.Note;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    List<Note> findByTitleIgnoreCaseContainingAndUserId(String title, Long userId);
    Optional<Note> findByTitleIgnoreCaseAndUserId(String title, Long userId);
    Optional<Note> findByCreatedAt(LocalDateTime createdAt);
    Optional<Note> findByTitleIgnoreCase(String title);
    List<Note> findByTitleIgnoreCaseContaining(String title);
}