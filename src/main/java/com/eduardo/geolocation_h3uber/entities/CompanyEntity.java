package com.eduardo.geolocation_h3uber.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private AddressEntity address;

    @OneToOne
    @JoinColumn(name= "user_id", unique = true)
    private UserEntity user;
}
