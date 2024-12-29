package com.example.plantBackend.repository;

import com.example.plantBackend.entities.UserPlant;
import com.example.plantBackend.sec.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPlantRepo extends JpaRepository<UserPlant, Long> {
    List<UserPlant> findByAppUser(AppUser appUser);
    UserPlant findByAppUserIdAndPlantIdPlante(Long userId, Long plantId);


}
