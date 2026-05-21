package com.eduardo.geolocation_h3uber.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logradouro;
    private String numero;
    private Double latitude;
    private Double longitude;

    // O índice H3 que usaremos para buscas rápidas
    @Column(name = "h3_index")
    private String h3Index;

    @OneToOne
    @JoinColumn(name = "client_id", unique = true)
    private ClientEntity client;

    @OneToOne
    @JoinColumn(name = "company_id", unique = true)
    private CompanyEntity company;
}
