package com.example.plantBackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer plantScan;
    private Integer remindersConfigured;
    private Integer chatbotInteractions;
    @Transient
    private Long usercount;
    @Transient
    private Long plantcount;


}
