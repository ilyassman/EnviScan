package com.example.planttest2.beans;

public class Reminder {
    private String date;
    private String type;
    private String plantsCount;

    public Reminder(String date, String type, String plantsCount) {
        this.date = date;
        this.type = type;
        this.plantsCount = plantsCount;
    }

    public String getDate() { return date; }
    public String getType() { return type; }
    public String getPlantsCount() { return plantsCount; }
}