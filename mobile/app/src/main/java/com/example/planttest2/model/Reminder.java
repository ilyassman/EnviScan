package com.example.planttest2.model;

public class Reminder {
    private String plantName;
    private String type;
    private String time;
    private String notes;

    public Reminder(String plantName, String type, String time, String notes) {
        this.plantName = plantName;
        this.type = type;
        this.time = time;
        this.notes = notes;
    }

    // Getters
    public String getPlantName() { return plantName; }
    public String getType() { return type; }
    public String getTime() { return time; }
    public String getNotes() { return notes; }
}