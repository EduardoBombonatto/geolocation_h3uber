package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.UserDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.events.AddressCreatedEvent;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import com.uber.h3core.H3Core;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final H3Core h3Core;
    private final ModelMapper modelMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);

        if (userEntity.getAddress() != null) {
            userEntity.getAddress().setUser(userEntity);
        }

        UserEntity savedUser = userRepository.save(userEntity);

        if (savedUser.getAddress() != null &&
                savedUser.getAddress().getLatitude() != null &&
                savedUser.getAddress().getLongitude() != null) {

            eventPublisher.publishEvent(new AddressCreatedEvent(
                    savedUser.getAddress().getId(),
                    savedUser.getAddress().getLatitude(),
                    savedUser.getAddress().getLongitude()
            ));
        }
        return modelMapper.map(savedUser, UserDTO.class);
    }

    public List<CompanyDTO> findNearbyCompanies(UUID userId, int radiusInHexagons) {
        // 1. Busca o usuário
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String userH3Index = user.getAddress().getH3Index();
        if (userH3Index == null) {
            throw new RuntimeException("Usuário não possui um endereço válido com índice H3");
        }

        // 2. Calcula os hexágonos vizinhos com base no raio (k-ring)
        // Se radiusInHexagons for 1, pega o hexágono atual e os vizinhos imediatos.
        List<String> neighbors = h3Core.gridDisk(userH3Index, radiusInHexagons);

        // 3. Busca no banco as empresas que estão dentro desses hexágonos
        List<CompanyEntity> nearbyCompanies = companyRepository.findAllByH3IndexIn(neighbors);

        // 4. Mapeia para DTO e retorna
        return nearbyCompanies.stream()
                .map(company -> modelMapper.map(company, CompanyDTO.class))
                .collect(Collectors.toList());
    }
}
