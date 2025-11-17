package uk.thisjowi.Password.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.thisjowi.Password.Entity.Password;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

    List<Password> findByName(String name);

    List<Password> findByUsername(String username);
    
    List<Password> findByUserId(Long userId);
}
