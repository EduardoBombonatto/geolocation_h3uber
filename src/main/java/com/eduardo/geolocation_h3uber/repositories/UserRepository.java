package com.eduardo.geolocation_h3uber.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.eduardo.geolocation_h3uber.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserDetails findByEmail(String email);
}
