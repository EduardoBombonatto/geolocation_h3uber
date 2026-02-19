package com.eduardo.geolocation_h3uber.controllers;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.UserDTO;
import com.eduardo.geolocation_h3uber.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{userId}/nearby-companies")
    public ResponseEntity<List<CompanyDTO>> getNearbyCompanies(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "1") int radius) {

        // O parâmetro 'radius' define a quantidade de anéis de hexágonos em redor do utilizador.
        // Um raio de 1 significa o hexágono atual + os vizinhos imediatos.
        List<CompanyDTO> nearbyCompanies = userService.findNearbyCompanies(userId, radius);
        return ResponseEntity.ok(nearbyCompanies);
    }
}
