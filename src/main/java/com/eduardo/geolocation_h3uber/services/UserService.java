package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressService enderecoService;

    @Transactional
    public UserEntity save(UserEntity user) {
        if (user.getAddress() != null) {
            // RF10: Transforma endereço em Lat/Long e gera o H3 Index
            enderecoService.processarGeolocalizacao(user.getAddress());

            // Vincula o endereço ao usuário (RF08)
            user.getAddress().setUser(user);
        }
        return userRepository.save(user);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public void delete(UUID id) {
        UserEntity user = findById(id);
        userRepository.delete(user);
    }

    @Transactional
    public UserEntity update(UUID id, UserEntity userUpdate) {
        UserEntity user = findById(id);

        user.setName(userUpdate.getName());
        user.setEmail(userUpdate.getEmail());

        if (userUpdate.getAddress() != null) {
            // Se o endereço mudou, reprocessamos o H3
            enderecoService.processarGeolocalizacao(userUpdate.getAddress());
            user.setAddress(userUpdate.getAddress());
            user.getAddress().setUser(user);
        }

        return userRepository.save(user);
    }
}
