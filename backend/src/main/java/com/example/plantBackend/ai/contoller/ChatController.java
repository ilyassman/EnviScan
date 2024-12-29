package com.example.plantBackend.ai.contoller;
import com.example.plantBackend.ai.model.Plant;
import com.example.plantBackend.repository.PlantRepo;
import com.example.plantBackend.sec.entity.AppUser;
import com.example.plantBackend.sec.repo.UserAppRepository;
import com.example.plantBackend.sec.services.AccountService;
import com.example.plantBackend.services.StatisticsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.plantBackend.ai.model.PlantSearch;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {
    private ChatClient chatClient;
    @Autowired
    private UserAppRepository userAppRepository;
    @Autowired
    private PlantRepo plantRepo;
    @Autowired
    private AccountService accountService;
    @Autowired
    private StatisticsService statisticsService;
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    @GetMapping(value = "/chat")
    public Plant chat(String message) {
        System.out.println("Received message: " + message);
        statisticsService.incrementPlantScan();

        // Appel à l'API pour récupérer les données de la plante
        Plant plant = chatClient.prompt()
                .system("L'utilisateur vous a donné un nom de plante. Répondez uniquement aux questions relatives à la plante. Fournissez une description simplifiée avec les informations suivantes : température idéale, lumière, arrosage, rempotage et fertilisation. Ajoutez les zones naturelles  où la plante est distribuée dans le maroc la zone doit contenire 4 point svp.")
                .user(message)
                .call()
                .entity(Plant.class);

        return plant;
    }
    @GetMapping(value = "/searchPlant")
    public List<PlantSearch> searchPlant(String message) {
        System.out.println("Received message: " + message);

        // Appel à l'API pour récupérer les données de la plante
        List<PlantSearch> plants = chatClient.prompt()
                .system("L'utilisateur va donner un nom et tu dois retourner une liste des plantes qui contiennent le name,urlimage,le nomScientifique et type")
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<PlantSearch>>() {}); // Utilisation de ParameterizedTypeReference

        return plants;
    }


    @GetMapping(value = "/chatboot")
    public ResponseEntity<Map<String, String>> chatboot(@RequestParam String message) {
        statisticsService.incrementChatbotInteractions();

        System.out.println("Received message: " + message);
        String systemMessage = "L'utilisateur vous a envoyé un message concernant une plante. Répondez uniquement à la question posée"
                + "Si la question ne concerne pas la plante, non repondre pas";

        String content = chatClient.prompt()
                .system(systemMessage)
                .user(message)
                .call()
                .content();

        // Créez une carte pour retourner la réponse sous forme de JSON
        Map<String, String> response = new HashMap<>();
        response.put("response", content);

        // Retournez la réponse dans un format JSON
        return ResponseEntity.ok(response);
    }
    @GetMapping(value = "/adminchatboot")
    public ResponseEntity<Map<String, String>> adminChatboot(@RequestParam String message) {
        System.out.println("Message reçu : " + message);
        statisticsService.incrementChatbotInteractions();
        // Message système spécifique pour l'admin
        String systemMessage = "Vous êtes un assistant virtuel pour l'administrateur. "
                + "Si une demande d'ajout d'utilisateur est faite, répondez UNIQUEMENT avec un format JSON précis: "
                + "{\"action\": \"add_user\", \"username\": \"nom_utilisateur\", \"email\": \"email_utilisateur\", \"password\": \"mot_de_passe\"}. "
                + "Si une demande de suppression d'utilisateur est faite, répondez UNIQUEMENT avec un format JSON précis: "
                + "{\"action\": \"supp_user\", \"username\": \"nom_utilisateur\"}. "
                + "Si aucune demande d'ajout ou suppresion n'est détectée, répondez normalement."
                + "si admin demande des infos sur app ca veut dire il veut des infos depuis la bdd"
                ;

        // Récupérer la liste des utilisateurs et des plantes depuis la base de données
        List<AppUser> users = userAppRepository.findAll();
        List<com.example.plantBackend.entities.Plant> plants = plantRepo.findAll();

        // Préparer les données sous forme de chaîne de caractères
        StringBuilder usersList = new StringBuilder("Liste des utilisateurs (données extraites de la base de données et de application)  : ");
        for (AppUser user : users) {
            usersList.append(user.getUsername()).append(", ");
        }

        StringBuilder plantsList = new StringBuilder("Liste des plantes (données extraites de la base de données et de application) : ");
        for (com.example.plantBackend.entities.Plant plant : plants) {
            plantsList.append(plant.getNomPlante()).append(", ");
        }

        // Créer un contexte incluant ces informations
        String additionalContext = usersList.toString() + "\n" + plantsList.toString();

        // Créez la requête OpenAI pour obtenir la réponse basée sur le message de l'administrateur
        String content = chatClient.prompt()
                .system(systemMessage)
                .user(message + "\n" + additionalContext)
                .call()
                .content();

        // Créez une carte pour retourner la réponse
        Map<String, String> response = new HashMap<>();

        try {
            // Utiliser Jackson pour parser le JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(content);

            // Vérifier si l'action est d'ajouter un utilisateur
            if (jsonResponse.has("action") && "add_user".equals(jsonResponse.get("action").asText())) {
                // Extraire les informations du nouvel utilisateur
                String username = jsonResponse.get("username").asText();
                String email = jsonResponse.get("email").asText();
                String password = jsonResponse.get("password").asText();

                // Vérifier si l'utilisateur existe déjà
                if (userAppRepository.findByUsername(username) != null) {
                    response.put("response", "Erreur : Un utilisateur avec ce nom existe déjà");
                    return ResponseEntity.ok(response);
                }

                // Créer un nouvel utilisateur
                AppUser newUser = new AppUser();
                newUser.setUsername(username);
                newUser.setEmail(email);

                // Encoder le mot de passe
                String encodedPassword =password;
                newUser.setPassword(encodedPassword);

                // Définir les rôles par défaut si nécessaire
                // newUser.setRoles(Collections.singleton(Role.USER));

                // Enregistrer l'utilisateur dans la base de données
                accountService.addNewAccount(newUser);

                // Préparer la réponse de succès
                response.put("response", "Utilisateur " + username + " ajouté avec succès");
                return ResponseEntity.ok(response);
            }
            else if (jsonResponse.has("action") && "supp_user".equals(jsonResponse.get("action").asText())) {
                String username = jsonResponse.get("username").asText();
                AppUser newUser = userAppRepository.findByUsername(username);
                if(newUser == null) {
                    response.put("response","Utilisateur non trouvé");
                }
                else{
                    userAppRepository.delete(newUser);
                    response.put("response", "Utilisateur " + username + " supprimé avec succès");
                }

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Gestion des erreurs de parsing ou autres
            response.put("response", content); // Renvoyer la réponse originale si pas de JSON
            System.err.println("Erreur de traitement : " + e.getMessage());
        }

        // Retourner la réponse par défaut
        return ResponseEntity.ok(response);
    }








}
