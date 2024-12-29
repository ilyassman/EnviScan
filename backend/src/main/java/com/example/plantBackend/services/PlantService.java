package com.example.plantBackend.services;

import com.example.plantBackend.entities.Plant;
import com.example.plantBackend.repository.PlantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {

    @Autowired
    PlantRepo plantRepo;
    public Long addPlant(Plant plant) {
        if(!plantRepo.findByNomPlante(plant.getNomPlante()).isPresent())
        plantRepo.save(plant);
        else
            plant=plantRepo.findByNomPlante(plant.getNomPlante()).get();
        return plant.getIdPlante();
    }
    public void updatePlant(Long id,Plant plant) {
        Plant plant1=plantRepo.findById(id).get();
        plant1.setNomPlante(plant.getNomPlante());
        plant1.setCategorie(plant.getCategorie());
        plant1.setNomScientifique(plant.getNomScientifique());
        if(plant.getImage() != null)
            plant1.setImage(plant.getImage());
        plantRepo.save(plant1);

    }
    public List<Plant> getAllPlants() {
        return plantRepo.findAll();
    }
    public Plant getPlantById(Long id) {
        return plantRepo.findById(id).orElse(null);
    }
    public void deletePlantById(Long id) {
        plantRepo.deleteById(id);
    }

}
