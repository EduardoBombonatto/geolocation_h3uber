package com.eduardo.geolocation_h3uber.controller;

import com.eduardo.geolocation_h3uber.dtos.UserDTO;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    // RF01: O sistema deve ser capaz de cadastrar usuário
    @PostMapping
    public ResponseEntity<UserDTO> cadastrar(@RequestBody UserDTO userDTO) {
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        UserEntity salvo = userService.save(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(salvo, UserDTO.class));
    }

    // RF02: O sistema deve ser capaz de atualizar o usuário
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> atualizar(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        UserEntity userUpdate = modelMapper.map(userDTO, UserEntity.class);
        UserEntity atualizado = userService.update(id, userUpdate);
        return ResponseEntity.ok(modelMapper.map(atualizado, UserDTO.class));
    }

    // RF03: O sistema deve ser capaz de excluir o usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> listarTodos() {
        List<UserDTO> usuarios = userService.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> buscarPorId(@PathVariable UUID id) {
        UserEntity user = userService.findById(id);
        return ResponseEntity.ok(modelMapper.map(user, UserDTO.class));
    }
}
