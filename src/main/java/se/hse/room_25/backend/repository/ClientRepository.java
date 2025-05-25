package se.hse.room_25.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.hse.room_25.backend.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUsername(String username);
}