package com.example.plantBackend.entities;

import com.example.plantBackend.sec.entity.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlante;
    private String nomPlante;
    private String nomScientifique;
    private String categorie;
    @Lob
    private byte[] image;
    @Transient // Ce champ ne doit pas être mappé à la base de données
    private String imageUrl; // Contient l'image encodée en base64
    @OneToMany(mappedBy = "plant", fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<UserPlant> userPlants = new ArrayList<>();
}
