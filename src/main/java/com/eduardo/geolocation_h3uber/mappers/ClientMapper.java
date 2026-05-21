package com.eduardo.geolocation_h3uber.mappers;

import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.dtos.CreateClientDTO;
import com.eduardo.geolocation_h3uber.entities.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "address", ignore = true)
    ClientEntity toEntity(CreateClientDTO dto);

    @Mapping(target = "email", source = "user.email")
    ClientDTO toDTO(ClientEntity entity);
}
