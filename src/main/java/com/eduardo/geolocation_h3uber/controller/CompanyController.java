package com.eduardo.geolocation_h3uber.controller;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<CompanyDTO> cadastrar(@RequestBody CompanyDTO dto){
        CompanyEntity company = modelMapper.map(dto, CompanyEntity.class);
        CompanyEntity save = companyService.saveCompnay(company);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(save, CompanyDTO.class));
    }

    @GetMapping("/{user_uuid}")
    public ResponseEntity<List<CompanyDTO>> buscarPerto(@PathVariable("user_uuid") UUID userUuid) {
        // RF09: Busca empresas próximas usando H3
        List<CompanyEntity> empresas = companyService.buscarPertoDoUsuario(userUuid);

        List<CompanyDTO> dtos = empresas.stream()
                .map(e -> modelMapper.map(e, CompanyDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
