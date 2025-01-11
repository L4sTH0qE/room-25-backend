package se.hse.room_25.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "session", schema = "public")
public class Session {

    @Id
    UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id", nullable = false)
    Client client;

    @Column(nullable = false)
    String token;

    @Column(nullable = false)
    Timestamp expiration;

    public Session() {
    }

    public Session(Client client, String token, Timestamp expiration) {
        this.client = client;
        this.token = token;
        this.expiration = expiration;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
