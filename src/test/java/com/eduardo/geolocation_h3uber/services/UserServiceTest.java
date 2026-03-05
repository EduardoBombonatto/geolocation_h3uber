package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.UserDTO;
import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.events.AddressCreatedEvent;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import com.uber.h3core.H3Core;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private H3Core h3Core;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user and publish event when user has valid address")
    void createUser_WithAddress_ShouldPublishEvent() {
        // Arrange
        UserDTO inputDTO = new UserDTO();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        inputDTO.setAddress(addressDTO);

        UserEntity userEntity = new UserEntity();
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setId(1L);
        addressEntity.setLatitude(-23.55052);
        addressEntity.setLongitude(-46.633308);
        userEntity.setAddress(addressEntity);

        when(modelMapper.map(inputDTO, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(inputDTO);

        // Act
        UserDTO result = userService.createUser(inputDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(userEntity);
        verify(eventPublisher, times(1)).publishEvent(any(AddressCreatedEvent.class));
        assertEquals(userEntity, addressEntity.getUser());
    }

    @Test
    @DisplayName("Should throw exception when address is missing during user creation")
    void createUser_WithoutAddress_ShouldThrowException() {
        // Arrange
        UserDTO inputDTO = new UserDTO();
        UserEntity userEntity = new UserEntity();
        userEntity.setAddress(null); // Explicitamente nulo

        when(modelMapper.map(inputDTO, UserEntity.class)).thenReturn(userEntity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            userService.createUser(inputDTO)
        );

        assertEquals("Endereço é obrigatório para criar um usuário", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Should find nearby companies successfully")
    void findNearbyCompanies_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int radius = 1;
        String userH3Index = "8a2a1072b59ffff";
        List<String> neighbors = List.of("8a2a1072b59ffff", "8a2a1072b5b7fff");

        UserEntity user = new UserEntity();
        AddressEntity userAddress = new AddressEntity();
        userAddress.setH3Index(userH3Index);
        user.setAddress(userAddress);

        CompanyEntity company1 = new CompanyEntity();
        CompanyEntity company2 = new CompanyEntity();
        List<CompanyEntity> companies = List.of(company1, company2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(h3Core.gridDisk(userH3Index, radius)).thenReturn(neighbors);
        when(companyRepository.findAllByH3IndexIn(neighbors)).thenReturn(companies);
        when(modelMapper.map(any(CompanyEntity.class), eq(CompanyDTO.class))).thenReturn(new CompanyDTO());

        // Act
        List<CompanyDTO> result = userService.findNearbyCompanies(userId, radius);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findById(userId);
        verify(h3Core, times(1)).gridDisk(userH3Index, radius);
        verify(companyRepository, times(1)).findAllByH3IndexIn(neighbors);
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void findNearbyCompanies_UserNotFound_ShouldThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            userService.findNearbyCompanies(userId, 1)
        );
        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user has no H3 index")
    void findNearbyCompanies_NoH3Index_ShouldThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        AddressEntity userAddress = new AddressEntity();
        userAddress.setH3Index(null); // No H3 Index
        user.setAddress(userAddress);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            userService.findNearbyCompanies(userId, 1)
        );
        assertEquals("Usuário não possui um endereço válido com índice H3", exception.getMessage());
    }
}
