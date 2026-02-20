package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.events.AddressCreatedEvent;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.uber.h3core.H3Core;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final H3Core h3Core;
    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO){
        CompanyEntity company = modelMapper.map(companyDTO, CompanyEntity.class);

        if (company.getAddress() != null) {
            company.getAddress().setCompany(company);
        }
        CompanyEntity savedCompany = companyRepository.save(company);

        if (savedCompany.getAddress() != null &&
                savedCompany.getAddress().getLatitude() != null &&
                savedCompany.getAddress().getLongitude() != null) {

            eventPublisher.publishEvent(new AddressCreatedEvent(
                    savedCompany.getAddress().getId(),
                    savedCompany.getAddress().getLatitude(),
                    savedCompany.getAddress().getLongitude()
            ));
        }

        return modelMapper.map(savedCompany, CompanyDTO.class);
    }

}