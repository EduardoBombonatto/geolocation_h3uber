package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.AddressDTO;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressEventService addressEventService;

    @InjectMocks
    private CompanyService companyService;

    @Test
    @DisplayName("Should create company and publish event when company has valid address")
    void createCompany_WithAddress_ShouldPublishEvent() {
        // Arrange
        CompanyDTO inputDTO = new CompanyDTO();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLatitude(-23.55052);
        addressDTO.setLongitude(-46.633308);
        inputDTO.setAddress(addressDTO);

        CompanyEntity companyEntity = new CompanyEntity();
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setId(1L);
        addressEntity.setLatitude(-23.55052);
        addressEntity.setLongitude(-46.633308);
        companyEntity.setAddress(addressEntity);

        when(modelMapper.map(inputDTO, CompanyEntity.class)).thenReturn(companyEntity);
        when(companyRepository.save(companyEntity)).thenReturn(companyEntity);
        when(modelMapper.map(companyEntity, CompanyDTO.class)).thenReturn(inputDTO);

        // Act
        CompanyDTO result = companyService.createCompany(inputDTO);

        // Assert
        assertNotNull(result);
        verify(companyRepository, times(1)).save(companyEntity);
        verify(addressEventService, times(1)).publishAddressCreatedEvent(addressEntity);
        assertEquals(companyEntity, addressEntity.getCompany());
    }

    @Test
    @DisplayName("Should throw exception when address is missing during company creation")
    void createCompany_WithoutAddress_ShouldThrowException() {
        // Arrange
        CompanyDTO inputDTO = new CompanyDTO();
        CompanyEntity companyEntity = new CompanyEntity();
        companyEntity.setAddress(null);

        when(modelMapper.map(inputDTO, CompanyEntity.class)).thenReturn(companyEntity);

        // Act & Assert
        AddressRequiredException exception = assertThrows(AddressRequiredException.class,
                () -> companyService.createCompany(inputDTO));

        assertEquals("Endereço é obrigatório para criar uma empresa", exception.getMessage());
        verify(companyRepository, never()).save(any());
        verify(addressEventService, never()).publishAddressCreatedEvent(any());
    }
}
