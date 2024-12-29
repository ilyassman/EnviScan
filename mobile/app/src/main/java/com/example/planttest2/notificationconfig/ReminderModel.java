package com.example.planttest2.notificationconfig;

public class ReminderModel {
    private int id;
    private String title;
    private String description;
    private long timeInMillis;

    // Constructeur, getters et setters
    public ReminderModel(int id, String title, String description, long timeInMillis) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeInMillis = timeInMillis;
    }

    // Getters et setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTimeInMillis() { return timeInMillis; }
}