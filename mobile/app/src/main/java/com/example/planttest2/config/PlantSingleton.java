package com.example.planttest2.config;

public class PlantSingleton {
    private static PlantSingleton instance;
    private String plantImage;

    private PlantSingleton() {
        // Private constructor to prevent instantiation
    }

    public static PlantSingleton getInstance() {
        if (instance == null) {
            instance = new PlantSingleton();
        }
        return instance;
    }

    public void setPlantImage(String image) {
        this.plantImage = image;
    }

    public String getPlantImage() {
        return plantImage;
    }
}
