package com.eduardo.geolocation_h3uber.services.integration;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.services.CompanyService;
import com.eduardo.geolocation_h3uber.utils.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CompanyServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("Should create company successfully in integration context")
    void createCompany_Integration_Success() {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setName("Integration Company");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLogradouro("Avenida de Integração");
        addressDTO.setNumero("1000");
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        companyDTO.setAddress(addressDTO);

        CompanyDTO result = companyService.createCompany(companyDTO);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Integration Company");
        assertThat(result.getAddress().getLogradouro()).isEqualTo("Avenida de Integração");

        CompanyEntity savedCompany = companyRepository.findById(result.getId()).orElseThrow();
        assertThat(savedCompany.getAddress()).isNotNull();
        assertThat(savedCompany.getAddress().getCompany()).isEqualTo(savedCompany);
    }
}
