package com.example.plantBackend.repository;
import com.example.plantBackend.entities.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    Statistics findFirstByOrderByIdAsc();
}
