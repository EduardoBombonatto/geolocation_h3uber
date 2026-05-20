package com.eduardo.geolocation_h3uber.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eduardo.geolocation_h3uber.entities.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
}
