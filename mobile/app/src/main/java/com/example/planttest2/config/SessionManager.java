package com.example.planttest2.config;

public class SessionManager {
    private static SessionManager instance;
    private String accessToken;

    private SessionManager() {
        // Constructeur privé pour empêcher l'instanciation directe
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }
}
