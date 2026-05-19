package com.eduardo.geolocation_h3uber.controllers;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.exceptions.H3IndexNotFoundException;
import com.eduardo.geolocation_h3uber.exceptions.ClientNotFoundException;
import com.eduardo.geolocation_h3uber.services.ClientService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@DisplayName("Client Controller Tests")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create a client and return 201 Created")
    void shouldCreateClient() throws Exception {
        // Arrange
        ClientDTO requestDto = new ClientDTO();
        requestDto.setName("John Doe");
        requestDto.setEmail("john.doe@example.com");

        ClientDTO responseDto = new ClientDTO();
        responseDto.setName("John Doe");
        responseDto.setEmail("john.doe@example.com");

        when(clientService.createClient(any(ClientDTO.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when address is required for client")
    void shouldReturn400WhenAddressIsRequired() throws Exception {
        // Arrange
        ClientDTO requestDto = new ClientDTO();
        requestDto.setName("Client Without Address");

        when(clientService.createClient(any(ClientDTO.class)))
                .thenThrow(new AddressRequiredException("Endereço é obrigatório"));

        // Act & Assert
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return a list of nearby companies")
    void shouldReturnNearbyCompanies() throws Exception {
        // Arrange
        UUID clientId = UUID.randomUUID();
        int radius = 2;

        CompanyDTO company1 = new CompanyDTO();
        company1.setName("Company A");

        CompanyDTO company2 = new CompanyDTO();
        company2.setName("Company B");

        List<CompanyDTO> nearbyCompanies = List.of(company1, company2);

        when(clientService.findNearbyCompanies(eq(clientId), eq(radius))).thenReturn(nearbyCompanies);

        // Act & Assert
        mockMvc.perform(get("/api/clients/{clientId}/nearby-companies", clientId)
                .param("radius", String.valueOf(radius))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Company A"))
                .andExpect(jsonPath("$[1].name").value("Company B"));
    }

    @Test
    @DisplayName("Should use default radius of 1 when not provided")
    void shouldReturnNearbyCompaniesWithDefaultRadius() throws Exception {
        // Arrange
        UUID clientId = UUID.randomUUID();
        int defaultRadius = 1;

        CompanyDTO company1 = new CompanyDTO();
        company1.setName("Company Default");

        List<CompanyDTO> nearbyCompanies = List.of(company1);

        when(clientService.findNearbyCompanies(eq(clientId), eq(defaultRadius))).thenReturn(nearbyCompanies);

        // Act & Assert
        mockMvc.perform(get("/api/clients/{clientId}/nearby-companies", clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Company Default"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when client is not found")
    void shouldReturn404WhenClientNotFound() throws Exception {
        // Arrange
        UUID clientId = UUID.randomUUID();
        when(clientService.findNearbyCompanies(eq(clientId), anyInt()))
                .thenThrow(new ClientNotFoundException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/clients/{clientId}/nearby-companies", clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when client has no H3 index")
    void shouldReturn400WhenNoH3Index() throws Exception {
        // Arrange
        UUID clientId = UUID.randomUUID();
        when(clientService.findNearbyCompanies(eq(clientId), anyInt()))
                .thenThrow(new H3IndexNotFoundException("Índice H3 não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/clients/{clientId}/nearby-companies", clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
