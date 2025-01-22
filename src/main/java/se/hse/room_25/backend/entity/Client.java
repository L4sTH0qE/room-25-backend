package se.hse.room_25.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

import lombok.Data;

@Data
@Entity
@Table(name = "client", schema = "public")
public class Client {

    @Id
    UUID id;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    public Client() {
    }

    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
