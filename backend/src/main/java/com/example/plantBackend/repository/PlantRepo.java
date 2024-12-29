package com.example.plantBackend.repository;

import com.example.plantBackend.entities.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantRepo extends JpaRepository<Plant, Long> {
    Optional<Plant> findByNomPlante(String nomPlante);
}
