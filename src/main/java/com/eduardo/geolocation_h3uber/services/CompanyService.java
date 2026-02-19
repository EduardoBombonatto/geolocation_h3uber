package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.uber.h3core.H3Core;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final H3Core h3Core;
    private final ModelMapper modelMapper;

    @Value("${h3.resolution}")
    private static int H3_RESOLUTION;

    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO){
        CompanyEntity company = modelMapper.map(companyDTO, CompanyEntity.class);

        if (company.getAddress() != null) {
            String h3Index = h3Core.latLngToCellAddress(
                    company.getAddress().getLatitude(),
                    company.getAddress().getLongitude(),
                    H3_RESOLUTION
            );
            company.getAddress().setH3Index(h3Index);
            company.getAddress().setCompany(company);
        }
        CompanyEntity savedCompany = companyRepository.save(company);
        return modelMapper.map(savedCompany, CompanyDTO.class);
    }

}