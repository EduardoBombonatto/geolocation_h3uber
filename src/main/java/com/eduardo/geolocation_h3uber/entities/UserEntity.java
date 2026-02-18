package com.eduardo.geolocation_h3uber.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;

    @OneToOne(mappedBy = "empresa", cascade = CascadeType.ALL)
    private AddressEntity address;
}
