package com.eduardo.geolocation_h3uber.controllers;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.services.CompanyService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyController.class)
@DisplayName("Company Controller Tests")
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create a company and return 201 Created")
    void shouldCreateCompany() throws Exception {
        // Arrange
        CompanyDTO requestDto = new CompanyDTO();
        requestDto.setName("Test Company");

        CompanyDTO responseDto = new CompanyDTO();
        responseDto.setName("Test Company");

        when(companyService.createCompany(any(CompanyDTO.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Company"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when address is required")
    void shouldReturn400WhenAddressIsRequired() throws Exception {
        // Arrange
        CompanyDTO requestDto = new CompanyDTO();
        requestDto.setName("Company Without Address");

        when(companyService.createCompany(any(CompanyDTO.class)))
                .thenThrow(new AddressRequiredException("Endereço é obrigatório"));

        // Act & Assert
        mockMvc.perform(post("/api/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
