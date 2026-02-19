package com.eduardo.geolocation_h3uber.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_addresses")
@Data
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String logradouro;
    private String numero;
    private Double latitude;
    private Double longitude;

    // O índice H3 que usaremos para buscas rápidas
    @Column(name = "h3_index")
    private String h3Index;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;
}
