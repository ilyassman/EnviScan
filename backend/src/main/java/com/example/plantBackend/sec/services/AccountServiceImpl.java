package com.example.plantBackend.sec.services;

import com.example.plantBackend.entities.Plant;
import com.example.plantBackend.entities.UserPlant;
import com.example.plantBackend.repository.UserPlantRepo;
import com.example.plantBackend.sec.entity.AppRole;
import com.example.plantBackend.sec.entity.AppUser;
import com.example.plantBackend.sec.repo.AppRoleRepository;
import com.example.plantBackend.sec.repo.UserAppRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private final UserAppRepository userAppRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPlantRepo userPlantRepository;


    public AccountServiceImpl(UserAppRepository userAppRepository,
                              AppRoleRepository appRoleRepository,
                              PasswordEncoder passwordEncoder, UserPlantRepo userPlantRepository) {
        this.userAppRepository = userAppRepository;
        this.appRoleRepository = appRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPlantRepository = userPlantRepository;
    }
    @Override
    public List<Plant> getPlantsByUser(String username) {
        AppUser appUser = userAppRepository.findByUsername(username);
        if (appUser == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // Récupérer les UserPlants associés à l'utilisateur et extraire les plantes
        List<UserPlant> userPlants = userPlantRepository.findByAppUser(appUser);

        // Extraire les plantes et convertir les images en base64
        return userPlants.stream()
                .map(userPlant -> {
                    Plant plant = userPlant.getPlant();
                    if (plant.getImage() != null) {
                        // Convertir l'image en base64
                        String base64Image = Base64.getEncoder().encodeToString(plant.getImage());
                        plant.setImageUrl(base64Image);  // Ajouter l'image en base64 à l'objet Plant
                    }
                    return plant;
                })
                .toList();
    }

    @Override
    public void updateUser(Long id,AppUser user) {
        AppUser user1=userAppRepository.findById(id).get();
        user1.setUsername(user.getUsername());
        if(user.getPassword()!=null)
        user1.setPassword(passwordEncoder.encode(user.getPassword()));
        user1.setEmail(user.getEmail());
        userAppRepository.save(user1);

    }


    @Override
    public AppUser addNewAccount(AppUser user) {
        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        return userAppRepository.save(user);
    }

    @Override
    public AppRole addNewRole(AppRole role) {
        return appRoleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String role) {
        AppUser appuser = userAppRepository.findByUsername(username);
        AppRole approle = appRoleRepository.findByRolename(role);
        appuser.getRoles().add(approle);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return userAppRepository.findByUsername(username);
    }

    @Override
    public AppUser loadUserByEmail(String email) {
        return userAppRepository.findByEmail(email);    }

    @Override
    public AppUser updatePassword(String email, String newPassword) {
        AppUser appuser = userAppRepository.findByEmail(email);
        appuser.setPassword(passwordEncoder.encode(newPassword));
        return userAppRepository.save(appuser);
    }

    @Override
    public List<AppUser> getUsers() {
        return userAppRepository.findAll();
    }
}