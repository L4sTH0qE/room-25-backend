package se.hse.room_25.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.hse.room_25.backend.entity.Room;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findById(UUID id);
}
