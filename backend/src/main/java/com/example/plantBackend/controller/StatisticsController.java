package com.example.plantBackend.controller;
import com.example.plantBackend.entities.Statistics;
import com.example.plantBackend.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PutMapping("/increment/plantScan")
    public Statistics incrementPlantScan() {
        return statisticsService.incrementPlantScan();
    }

    @PutMapping("/increment/remindersConfigured")
    public Statistics incrementRemindersConfigured() {
        return statisticsService.incrementRemindersConfigured();
    }

    @PutMapping("/increment/chatbotInteractions")
    public Statistics incrementChatbotInteractions() {
        return statisticsService.incrementChatbotInteractions();
    }
    @GetMapping
    public Statistics getAllStatistics() {
        return statisticsService.getStatistics();
    }
}
