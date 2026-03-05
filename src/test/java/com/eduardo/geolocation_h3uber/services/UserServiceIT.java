package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.UserDTO;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
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
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save user in database with valid data")
    void createUser_IntegrationTest() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Eduardo");
        userDTO.setEmail("eduardo@example.com");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLogradouro("Rua Teste");
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        userDTO.setAddress(addressDTO);

        // Act
        UserDTO savedUserDTO = userService.createUser(userDTO);

        // Assert
        assertNotNull(savedUserDTO.getId());
        
        // Verifica se realmente está no banco de dados
        UserEntity databaseUser = userRepository.findById(savedUserDTO.getId()).orElse(null);
        assertNotNull(databaseUser);
        assertEquals("Eduardo", databaseUser.getName());
        assertNotNull(databaseUser.getAddress());
        assertEquals(-23.55052, databaseUser.getAddress().getLatitude());
    }
}
