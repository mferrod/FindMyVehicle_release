package dev.marianof.findmyvehicleapi.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.marianof.findmyvehicleapi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
