package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompanyServiceIT {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save company in database with valid data")
    void createCompany_IntegrationTest() {
        // Arrange
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setName("Tech Uber");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLogradouro("Avenida Paulista");
        addressDTO.setLatitude(-23.561414);
        addressDTO.setLongitude(-46.655881);
        companyDTO.setAddress(addressDTO);

        // Act
        CompanyDTO savedCompanyDTO = companyService.createCompany(companyDTO);

        // Assert
        assertNotNull(savedCompanyDTO.getId());
        
        // Verifica persistência real
        CompanyEntity databaseCompany = companyRepository.findById(savedCompanyDTO.getId()).orElse(null);
        assertNotNull(databaseCompany);
        assertEquals("Tech Uber", databaseCompany.getName());
        assertNotNull(databaseCompany.getAddress());
        assertEquals(-23.561414, databaseCompany.getAddress().getLatitude());
    }
}
