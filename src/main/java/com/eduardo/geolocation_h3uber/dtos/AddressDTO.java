package com.eduardo.geolocation_h3uber.dtos;

public record AddressDTO(
        Long id,
        String logradouro,
        String numero,
        Double latitude,
        Double longitude,
        String h3Index
) {
}
