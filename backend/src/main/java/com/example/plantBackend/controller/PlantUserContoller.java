package com.example.plantBackend.controller;

import com.example.plantBackend.entities.UserPlant;
import com.example.plantBackend.services.PlantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/jardin")
public class PlantUserContoller {
    @Autowired
    PlantUserService plantUserService;
    @GetMapping
    public List<UserPlant> getPlantUsers() {
        return plantUserService.getAllUserPlant();
    }
    @PostMapping
    public void addUserPlant(@RequestBody UserPlant userPlant, Principal principal) {
         plantUserService.addUserPlant(userPlant,principal);
    }
    @DeleteMapping("/{plantId}")
    public void removeUserPlant(Principal principal, @PathVariable Long plantId) {
        plantUserService.removeUserPlant(principal, plantId);
    }
}
