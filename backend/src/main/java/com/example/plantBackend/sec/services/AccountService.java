package com.example.plantBackend.sec.services;


import com.example.plantBackend.entities.Plant;
import com.example.plantBackend.sec.entity.AppRole;
import com.example.plantBackend.sec.entity.AppUser;

import java.util.List;

public interface AccountService {
    AppUser addNewAccount(AppUser user);
    AppRole addNewRole(AppRole role);
    void addRoleToUser(String username, String role);
    AppUser loadUserByUsername(String username);
    AppUser loadUserByEmail(String email);
    AppUser updatePassword(String username, String newPassword);
    List<AppUser> getUsers();
    public List<Plant> getPlantsByUser(String username);
    public void updateUser(Long id,AppUser user);
}
