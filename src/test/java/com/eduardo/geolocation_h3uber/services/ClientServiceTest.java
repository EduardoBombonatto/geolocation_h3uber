package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.ClientEntity;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.exceptions.H3IndexNotFoundException;
import com.eduardo.geolocation_h3uber.exceptions.ClientNotFoundException;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private GeolocationService geolocationService;

    @Mock
    private AddressEventService addressEventService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    @DisplayName("Should create client and publish event when client has valid address")
    void createClient_WithAddress_ShouldPublishEvent() {
        // Arrange
        ClientDTO inputDTO = new ClientDTO();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        inputDTO.setAddress(addressDTO);

        ClientEntity clientEntity = new ClientEntity();
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setId(1L);
        addressEntity.setLatitude(-23.55052);
        addressEntity.setLongitude(-46.633308);
        clientEntity.setAddress(addressEntity);

        when(modelMapper.map(inputDTO, ClientEntity.class)).thenReturn(clientEntity);
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);
        when(modelMapper.map(clientEntity, ClientDTO.class)).thenReturn(inputDTO);

        // Act
        ClientDTO result = clientService.createClient(inputDTO);

        // Assert
        assertNotNull(result);
        verify(clientRepository, times(1)).save(clientEntity);
        verify(addressEventService, times(1)).publishAddressCreatedEvent(addressEntity);
        assertEquals(clientEntity, addressEntity.getClient());
    }

    @Test
    @DisplayName("Should throw exception when address is missing during client creation")
    void createClient_WithoutAddress_ShouldThrowException() {
        // Arrange
        ClientDTO inputDTO = new ClientDTO();
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setAddress(null); // Explicitamente nulo

        when(modelMapper.map(inputDTO, ClientEntity.class)).thenReturn(clientEntity);

        // Act & Assert
        AddressRequiredException exception = assertThrows(AddressRequiredException.class, () -> 
            clientService.createClient(inputDTO)
        );

        assertEquals("Endereço é obrigatório para criar um usuário", exception.getMessage());
        verify(clientRepository, never()).save(any());
        verify(addressEventService, never()).publishAddressCreatedEvent(any());
    }

    @Test
    @DisplayName("Should find nearby companies successfully")
    void findNearbyCompanies_Success() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        int radius = 1;
        String clientH3Index = "8a2a1072b59ffff";
        List<String> neighbors = List.of("8a2a1072b59ffff", "8a2a1072b5b7fff");

        ClientEntity client = new ClientEntity();
        AddressEntity clientAddress = new AddressEntity();
        clientAddress.setH3Index(clientH3Index);
        client.setAddress(clientAddress);

        CompanyEntity company1 = new CompanyEntity();
        CompanyEntity company2 = new CompanyEntity();
        List<CompanyEntity> companies = List.of(company1, company2);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(geolocationService.findNeighbors(clientH3Index, radius)).thenReturn(neighbors);
        when(companyRepository.findAllByH3IndexIn(neighbors)).thenReturn(companies);
        when(modelMapper.map(any(CompanyEntity.class), eq(CompanyDTO.class))).thenReturn(new CompanyDTO());

        // Act
        List<CompanyDTO> result = clientService.findNearbyCompanies(clientId, radius);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(clientRepository, times(1)).findById(clientId);
        verify(geolocationService, times(1)).findNeighbors(clientH3Index, radius);
        verify(companyRepository, times(1)).findAllByH3IndexIn(neighbors);
    }

    @Test
    @DisplayName("Should throw exception when client is not found")
    void findNearbyCompanies_ClientNotFound_ShouldThrowException() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () -> 
            clientService.findNearbyCompanies(clientId, 1)
        );
        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when client has no H3 index")
    void findNearbyCompanies_NoH3Index_ShouldThrowException() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        ClientEntity client = new ClientEntity();
        AddressEntity clientAddress = new AddressEntity();
        clientAddress.setH3Index(null); // No H3 Index
        client.setAddress(clientAddress);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        // Act & Assert
        H3IndexNotFoundException exception = assertThrows(H3IndexNotFoundException.class, () -> 
            clientService.findNearbyCompanies(clientId, 1)
        );
        assertEquals("Usuário não possui um endereço válido com índice H3", exception.getMessage());
    }
}
