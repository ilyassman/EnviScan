package com.example.plantBackend.controller;

import com.example.plantBackend.entities.Plant;
import com.example.plantBackend.services.PlantService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plants")
public class PlantController {
    @Autowired
    PlantService plantService;
    @GetMapping
    public List<Plant> getAllPlants() {
        return plantService.getAllPlants();
    }
    @PostMapping
    public Long addPlant(@RequestBody Plant plant) {
      return  plantService.addPlant(plant);
    }
    @DeleteMapping("/{id}")
    public void deletePlant(@PathVariable Long id) {
        plantService.deletePlantById(id);
    }
    @PutMapping("/{id}")
    public void updatePlant(@PathVariable Long id, @RequestBody Plant plant) {
        plantService.updatePlant(id, plant);
    }
}
