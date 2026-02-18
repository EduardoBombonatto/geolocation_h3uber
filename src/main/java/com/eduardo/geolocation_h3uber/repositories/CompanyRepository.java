package com.eduardo.geolocation_h3uber.repositories;

import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {
    @Query("SELECT e FROM CompanyEntity e JOIN e.address addr WHERE addr.h3Index IN :h3Neighbors")
    List<CompanyEntity> findAllByH3IndexIN(@Param("h3Neighbors") List<String> h3Neighbors);
}
