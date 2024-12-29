package com.example.plantBackend.sec.repo;

import com.example.plantBackend.sec.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    AppRole findByRolename(String name);
}
