package com.eduardo.geolocation_h3uber.mappers;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.CreateCompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "address", ignore = true)
    CompanyEntity toEntity(CreateCompanyDTO dto);

    CompanyDTO toDTO(CompanyEntity entity);
}
