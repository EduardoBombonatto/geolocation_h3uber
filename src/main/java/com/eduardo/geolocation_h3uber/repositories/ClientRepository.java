package com.eduardo.geolocation_h3uber.repositories;

import com.eduardo.geolocation_h3uber.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
}
