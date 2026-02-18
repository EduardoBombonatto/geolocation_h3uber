package com.eduardo.geolocation_h3uber.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private AddressDTO address;
}
