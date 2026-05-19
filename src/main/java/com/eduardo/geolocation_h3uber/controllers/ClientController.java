package com.eduardo.geolocation_h3uber.controllers;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.createClient(clientDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
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
