package com.eduardo.geolocation_h3uber.dtos;

import lombok.Data;

@Data
public class AddressDTO {
    private String logradouro;
    private String numero;
    private Double latitude;
    private Double longitude;
    private String h3Index; // Preenchido apenas na resposta
}
