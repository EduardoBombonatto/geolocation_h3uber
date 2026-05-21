package com.eduardo.geolocation_h3uber.controllers;

import com.eduardo.geolocation_h3uber.config.security.TokenService;
import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.CreateClientDTO;
import com.eduardo.geolocation_h3uber.dtos.TokenResponseDTO;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import com.eduardo.geolocation_h3uber.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public ClientController(ClientService clientService, TokenService tokenService, UserRepository userRepository) {
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenResponseDTO> createClient(@Valid @RequestBody CreateClientDTO clientDTO) {
        this.clientService.createClient(clientDTO);
        UserEntity newUser = userRepository.findByEmail(clientDTO.email())
                .orElseThrow(() -> new RuntimeException("Erro ao recuperar o usuário após o cadastro."));

        String accessToken = tokenService.generateAccessToken(newUser);
        String refreshToken = tokenService.generateRefreshToken(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TokenResponseDTO(accessToken, refreshToken));
    }

    @GetMapping("/{clientId}/nearby-companies")
    public ResponseEntity<List<CompanyDTO>> getNearbyCompanies(
            @PathVariable UUID clientId,
            @RequestParam(defaultValue = "1") int radius) {

        // O parâmetro 'radius' define a quantidade de anéis de hexágonos em redor do utilizador.
        // Um raio de 1 significa o hexágono atual + os vizinhos imediatos.
        List<CompanyDTO> nearbyCompanies = clientService.findNearbyCompanies(clientId, radius);
        return ResponseEntity.ok(nearbyCompanies);
    }
}
