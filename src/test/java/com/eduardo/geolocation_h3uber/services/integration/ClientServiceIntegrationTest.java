package com.eduardo.geolocation_h3uber.services.integration;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.ClientEntity;
import com.eduardo.geolocation_h3uber.repositories.AddressRepository;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.ClientRepository;
import com.eduardo.geolocation_h3uber.services.ClientService;
import com.eduardo.geolocation_h3uber.utils.BaseIntegrationTest;
import com.uber.h3core.H3Core;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ClientServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private H3Core h3Core;

    @Test
    @DisplayName("Should create client successfully in integration context")
    void createClient_Integration_Success() {
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setName("Integration Client");
        clientDTO.setEmail("integration@example.com");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLogradouro("Rua de Integração");
        addressDTO.setNumero("500");
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        clientDTO.setAddress(addressDTO);

        ClientDTO result = clientService.createClient(clientDTO);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Integration Client");
        assertThat(result.getAddress().getLogradouro()).isEqualTo("Rua de Integração");

        ClientEntity savedClient = clientRepository.findById(result.getId()).orElseThrow();
        assertThat(savedClient.getAddress()).isNotNull();
        assertThat(savedClient.getAddress().getClient()).isEqualTo(savedClient);
    }

    @Test
    @DisplayName("Should find nearby companies in integration context")
    void findNearbyCompanies_Integration_Success() throws Exception {
        // Arrange: Create a client with a manual H3 index
        String clientH3Index = h3Core.latLngToCellAddress(-23.55052, -46.633308, 6);
        
        ClientEntity client = new ClientEntity();
        client.setName("Finder Client");

        AddressEntity clientAddress = new AddressEntity();
        clientAddress.setH3Index(clientH3Index);
        clientAddress.setClient(client);
        client.setAddress(clientAddress);
        
        clientRepository.save(client);

        // Arrange: Create a company nearby (same index)
        CompanyEntity nearbyCompany = new CompanyEntity();
        nearbyCompany.setName("Nearby Company");

        AddressEntity companyAddress = new AddressEntity();
        companyAddress.setH3Index(clientH3Index);
        companyAddress.setCompany(nearbyCompany);
        nearbyCompany.setAddress(companyAddress);
        
        companyRepository.save(nearbyCompany);

        // Arrange: Create a far company (different index, not neighbor)
        String farH3Index = h3Core.latLngToCellAddress(-20.0, -40.0, 6);
        CompanyEntity farCompany = new CompanyEntity();
        farCompany.setName("Far Company");

        AddressEntity farAddress = new AddressEntity();
        farAddress.setH3Index(farH3Index);
        farAddress.setCompany(farCompany);
        farCompany.setAddress(farAddress);
        
        companyRepository.save(farCompany);

        // Act
        List<CompanyDTO> nearby = clientService.findNearbyCompanies(client.getId(), 1);

        // Assert
        assertThat(nearby).hasSize(1);
        assertThat(nearby.get(0).getName()).isEqualTo("Nearby Company");
    }
}
