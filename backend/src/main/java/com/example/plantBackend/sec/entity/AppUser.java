package com.example.plantBackend.sec.entity;

import com.example.plantBackend.entities.Plant;
import com.example.plantBackend.entities.UserPlant;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private boolean isGoogleAuth;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Add the creationDate field
    private LocalDateTime creationDate;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppRole> roles = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Collection<UserPlant> userPlants = new ArrayList<>();

    // Automatically set creationDate before persisting the entity
    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }
}
