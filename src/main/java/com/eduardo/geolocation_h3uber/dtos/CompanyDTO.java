package com.eduardo.geolocation_h3uber.dtos;

import java.util.UUID;

public record CompanyDTO(UUID id, String name, AddressDTO address) {
}
