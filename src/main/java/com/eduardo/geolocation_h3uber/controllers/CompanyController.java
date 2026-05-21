package com.eduardo.geolocation_h3uber.controllers;

import com.eduardo.geolocation_h3uber.config.security.TokenService;
import com.eduardo.geolocation_h3uber.dtos.CreateCompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.TokenResponseDTO;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import com.eduardo.geolocation_h3uber.services.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;
    private final TokenService tokenService;
    private final UserRepository userRepository;


    public CompanyController(CompanyService companyService, TokenService tokenService, UserRepository userRepository) {
        this.companyService = companyService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenResponseDTO> createCompany(@Valid @RequestBody CreateCompanyDTO companyDTO){
        this.companyService.createCompany(companyDTO);
        UserEntity newUser = userRepository.findByEmail(companyDTO.email())
                .orElseThrow(() -> new RuntimeException("Erro ao recuperar o usuário após o cadastro."));

        String accessToken = tokenService.generateAccessToken(newUser);
        String refreshToken = tokenService.generateRefreshToken(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TokenResponseDTO(accessToken, refreshToken));
    }
}
