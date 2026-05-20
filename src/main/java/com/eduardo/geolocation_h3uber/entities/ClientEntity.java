package com.eduardo.geolocation_h3uber.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "tb_clients")
@Data
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private AddressEntity address;

    @OneToOne(mappedBy = "company")
    private UserEntity user;
}
