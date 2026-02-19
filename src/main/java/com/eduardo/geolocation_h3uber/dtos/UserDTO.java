package com.eduardo.geolocation_h3uber.dtos;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String name,
        String email,
        AddressDTO address
) {
}
