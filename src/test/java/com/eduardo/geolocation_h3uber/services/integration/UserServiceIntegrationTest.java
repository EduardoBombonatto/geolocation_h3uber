package com.eduardo.geolocation_h3uber.services.integration;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.UserDTO;
import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.repositories.AddressRepository;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import com.eduardo.geolocation_h3uber.services.UserService;
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
class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private H3Core h3Core;

    @Test
    @DisplayName("Should create user successfully in integration context")
    void createUser_Integration_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Integration User");
        userDTO.setEmail("integration@example.com");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLogradouro("Rua de Integração");
        addressDTO.setNumero("500");
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        userDTO.setAddress(addressDTO);

        UserDTO result = userService.createUser(userDTO);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Integration User");
        assertThat(result.getAddress().getLogradouro()).isEqualTo("Rua de Integração");

        UserEntity savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertThat(savedUser.getAddress()).isNotNull();
        assertThat(savedUser.getAddress().getUser()).isEqualTo(savedUser);
    }

    @Test
    @DisplayName("Should find nearby companies in integration context")
    void findNearbyCompanies_Integration_Success() throws Exception {
        // Arrange: Create a user with a manual H3 index
        String userH3Index = h3Core.latLngToCellAddress(-23.55052, -46.633308, 6);
        
        UserEntity user = new UserEntity();
        user.setName("Finder User");

        AddressEntity userAddress = new AddressEntity();
        userAddress.setH3Index(userH3Index);
        userAddress.setUser(user);
        user.setAddress(userAddress);
        
        userRepository.save(user);

        // Arrange: Create a company nearby (same index)
        CompanyEntity nearbyCompany = new CompanyEntity();
        nearbyCompany.setName("Nearby Company");

        AddressEntity companyAddress = new AddressEntity();
        companyAddress.setH3Index(userH3Index);
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
        List<CompanyDTO> nearby = userService.findNearbyCompanies(user.getId(), 1);

        // Assert
        assertThat(nearby).hasSize(1);
        assertThat(nearby.get(0).getName()).isEqualTo("Nearby Company");
    }
}
