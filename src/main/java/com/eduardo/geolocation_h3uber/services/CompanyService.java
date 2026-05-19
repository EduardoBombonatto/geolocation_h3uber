package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;
    private final AddressEventService addressEventService;

    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        CompanyEntity company = modelMapper.map(companyDTO, CompanyEntity.class);

        validateAddress(company);

        company.getAddress().setCompany(company);
        CompanyEntity savedCompany = companyRepository.save(company);

        addressEventService.publishAddressCreatedEvent(savedCompany.getAddress());

        return modelMapper.map(savedCompany, CompanyDTO.class);
    }

    private void validateAddress(CompanyEntity company) {
        if (company.getAddress() == null) {
            throw new AddressRequiredException("Endereço é obrigatório para criar uma empresa");
        }
    }
}