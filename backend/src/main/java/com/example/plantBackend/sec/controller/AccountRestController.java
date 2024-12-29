package com.example.plantBackend.sec.controller;

import com.example.plantBackend.entities.Plant;
import com.example.plantBackend.sec.entity.AppUser;
import com.example.plantBackend.sec.repo.UserAppRepository;
import com.example.plantBackend.sec.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Month;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
public class AccountRestController {
    private AccountService accountService;
    @Autowired
    private UserAppRepository userAppRepository;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping("/users")
    public List<AppUser> appUsers(){
        return accountService.getUsers();
    }
    @PostMapping("/user")
    public AppUser addUser(@RequestBody AppUser user){
        AppUser user1=accountService.loadUserByUsername(user.getUsername());
        if(user1==null)
        return accountService.addNewAccount(user);
        else
            return null;
    }
    @DeleteMapping("user/{id}")
    public void deleteUser(@PathVariable Long id){
        userAppRepository.deleteById(id);
    }
    @PutMapping("/userupdate/{id}")
    public void updateUser(@PathVariable Long id,@RequestBody AppUser user){
        accountService.updateUser(id,user);

    }
    @PutMapping("/userupdatePassword")
    public AppUser updateUserPass(@RequestBody AppUser user){
        return accountService.updatePassword(user.getEmail(),user.getPassword());

    }
    @GetMapping("/profil")
    public AppUser profile(Principal principal){
        return accountService.loadUserByUsername(principal.getName());
    }
    @GetMapping("/user/plants")
    public List<Plant> getUserPlants(Principal principal) {
        // Utiliser le principal pour récupérer les plantes de l'utilisateur connecté
        return accountService.getPlantsByUser(principal.getName());
    }
    @GetMapping("/usersPerMonth")
    public Map<String, Integer> getUsersPerMonth() {
        // Fetch user count data for each month from the database
        List<Object[]> userCountData = userAppRepository.countUsersByMonth();

        // Create a map for all months initialized to 0
        Map<String, Integer> usersPerMonth = new LinkedHashMap<>();

        // Initialize all months to 0
        for (Month month : Month.values()) {
            usersPerMonth.put(month.name(), 0);  // Set default value to 0
        }

        // Populate the map with actual data from the query
        for (Object[] row : userCountData) {
            int monthNumber = (int) row[0];  // Get month number (1-12)
            long userCount = (long) row[1];  // Get the user count for that month

            // Convert month number to month name
            String monthName = Month.of(monthNumber).name();

            // Update the map with the actual user count for that month
            usersPerMonth.put(monthName, (int) userCount);
        }

        // Return the map (months will be in order from January to December)
        return usersPerMonth;
    }
}
