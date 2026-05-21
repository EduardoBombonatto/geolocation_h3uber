package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.CreateCompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.entities.UserRole;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.mappers.CompanyMapper;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final AddressEventService addressEventService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyMapper companyMapper;

    @Transactional
    public CompanyDTO createCompany(CreateCompanyDTO companyDTO) {
        if (userRepository.findByEmail(companyDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email ja existe no sistema");
        }

        CompanyEntity company = companyMapper.toEntity(companyDTO);

        UserEntity user = this.createUserObject(companyDTO);
        user = userRepository.save(user);

        company.setUser(user);

        validateAddress(company);
        company.getAddress().setCompany(company);

        CompanyEntity savedCompany = companyRepository.save(company);

        addressEventService.publishAddressCreatedEvent(savedCompany.getAddress());

        return companyMapper.toDTO(savedCompany);
    }

    private void validateAddress(CompanyEntity company) {
        if (company.getAddress() == null) {
            throw new AddressRequiredException("Endereço é obrigatório para criar uma empresa");
        }
    }

    private UserEntity createUserObject(CreateCompanyDTO company) {
        UserEntity user = new UserEntity();
        user.setEmail(company.email());
        user.setPassword(passwordEncoder.encode(company.password()));
        user.setRole(UserRole.COMPANY);
        return user;
    }
}