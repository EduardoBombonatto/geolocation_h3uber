package com.eduardo.geolocation_h3uber.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class CompanyDTO {
    private UUID id;
    private String nome;
    private String cnpj;
    private AddressDTO endereco;
}
