package com.example.plantBackend.services;

import com.example.plantBackend.entities.Statistics;
import com.example.plantBackend.repository.PlantRepo;
import com.example.plantBackend.repository.StatisticsRepository;
import com.example.plantBackend.sec.repo.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private PlantRepo plantRepository;
    @Autowired
    private UserAppRepository appRepository;

    public Statistics incrementPlantScan() {
        Statistics stats = statisticsRepository.findFirstByOrderByIdAsc();
        if (stats != null) {
            stats.setPlantScan(stats.getPlantScan() + 1);
            return statisticsRepository.save(stats);
        }
        return null; // Gérer le cas où stats ne sont pas trouvées (cas d'initialisation)
    }

    public Statistics incrementRemindersConfigured() {
        Statistics stats = statisticsRepository.findFirstByOrderByIdAsc();
        if (stats != null) {
            stats.setRemindersConfigured(stats.getRemindersConfigured() + 1);
            return statisticsRepository.save(stats);
        }
        return null;
    }

    public Statistics incrementChatbotInteractions() {
        Statistics stats = statisticsRepository.findFirstByOrderByIdAsc();
        if (stats != null) {
            stats.setChatbotInteractions(stats.getChatbotInteractions() + 1);
            return statisticsRepository.save(stats);
        }
        return null;
    }
    public Statistics getStatistics() {
        Statistics stats = statisticsRepository.findFirstByOrderByIdAsc();
        stats.setPlantcount(plantRepository.count());
        stats.setUsercount(appRepository.count());
        return stats;
    }
}
