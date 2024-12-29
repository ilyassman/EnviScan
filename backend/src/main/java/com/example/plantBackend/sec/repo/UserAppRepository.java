package com.example.plantBackend.sec.repo;

import com.example.plantBackend.sec.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserAppRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    AppUser findByEmail(String email);
    // Query to count users created in each month (returns a list of object arrays with month and count)
    @Query("SELECT FUNCTION('MONTH', u.creationDate), COUNT(u) FROM AppUser u GROUP BY FUNCTION('MONTH', u.creationDate) ORDER BY FUNCTION('MONTH', u.creationDate)")
    List<Object[]> countUsersByMonth();
}
