package com.example.planttest2.model;

import java.util.List;

public class Utilisateur {

    private Long id;

    private String username;
    private String password;
    private String email;
    private boolean googleAuth;


    public Utilisateur() {
    }

    public Utilisateur(String username, String email,String password) {
        this.username = username;
        this.password=password;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    // Getters et Setters


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }




    @Override
    public String toString() {
        return "User{" +

                ", username='" + username + '\'' +
                ", email='" + email + '\'' +

                '}';
    }

    public boolean isGoogleAuth() {
        return googleAuth;
    }

    public void setGoogleAuth(boolean googleAuth) {
        this.googleAuth = googleAuth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
