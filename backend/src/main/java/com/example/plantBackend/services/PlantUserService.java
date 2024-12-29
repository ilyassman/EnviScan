package com.example.plantBackend.services;

import com.example.plantBackend.entities.UserPlant;
import com.example.plantBackend.repository.UserPlantRepo;
import com.example.plantBackend.sec.entity.AppUser;
import com.example.plantBackend.sec.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PlantUserService {
    @Autowired
    UserPlantRepo userPlantRepo;
    @Autowired
    private AccountService accountService;
    public List<UserPlant> getAllUserPlant() {
        return userPlantRepo.findAll();
    }
    public void addUserPlant(UserPlant userPlant, Principal principal) {
        userPlant.setDateCreation(LocalDate.now());
        userPlant.setAppUser(accountService.loadUserByUsername(principal.getName()));
        userPlantRepo.save(userPlant);
    }
    public void removeUserPlant(Principal principal, Long plantId) {
        UserPlant userPlant = userPlantRepo.findByAppUserIdAndPlantIdPlante(accountService.loadUserByUsername(principal.getName()).getId(), plantId);
        if (userPlant != null) {
            userPlantRepo.delete(userPlant);
        }
    }
}
