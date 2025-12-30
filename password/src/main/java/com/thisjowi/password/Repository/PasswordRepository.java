package com.thisjowi.password.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thisjowi.password.Entity.Password;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {

    List<Password> findByName(String name);
    
    List<Password> findByUserId(Long userId);
}
