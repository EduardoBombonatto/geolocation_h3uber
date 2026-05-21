package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.dtos.CreateClientDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.ClientEntity;
import com.eduardo.geolocation_h3uber.entities.UserEntity;
import com.eduardo.geolocation_h3uber.entities.UserRole;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.exceptions.H3IndexNotFoundException;
import com.eduardo.geolocation_h3uber.exceptions.ClientNotFoundException;
import com.eduardo.geolocation_h3uber.mappers.ClientMapper;
import com.eduardo.geolocation_h3uber.mappers.CompanyMapper;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.ClientRepository;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;
    private final GeolocationService geolocationService;
    private final AddressEventService addressEventService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientMapper clientMapper;
    private final CompanyMapper companyMapper;

    @Transactional
    public ClientDTO createClient(CreateClientDTO clientDTO) {
        if (userRepository.findByEmail(clientDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email ja cadastrado.");
        }

        ClientEntity client = clientMapper.toEntity(clientDTO);
        UserEntity user = this.createUserObject(clientDTO);
        user = userRepository.save(user);

        client.setUser(user);

        this.validateAddress(client);
        client.getAddress().setClient(client);

        ClientEntity savedClient = clientRepository.save(client);
        addressEventService.publishAddressCreatedEvent(savedClient.getAddress());
        return clientMapper.toDTO(savedClient);

    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> findNearbyCompanies(UUID clientId, int radiusInHexagons) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Usuário não encontrado"));

        String clientH3Index = getClientH3Index(client);

        List<String> neighbors = geolocationService.findNeighbors(clientH3Index, radiusInHexagons);

        List<CompanyEntity> nearbyCompanies = companyRepository.findAllByH3IndexIn(neighbors);

        return nearbyCompanies.stream()
                .map(companyMapper::toDTO)
                .toList();
    }

    private void validateAddress(ClientEntity client) {
        if (client.getAddress() == null) {
            throw new AddressRequiredException("Endereço é obrigatório para criar um usuário");
        }
    }

    private String getClientH3Index(ClientEntity client) {
        String h3Index = client.getAddress().getH3Index();
        if (h3Index == null) {
            throw new H3IndexNotFoundException("Usuário não possui um endereço válido com índice H3");
        }
        return h3Index;
    }

    private UserEntity createUserObject(CreateClientDTO client) {
        UserEntity user = new UserEntity();
        user.setEmail(client.email());
        user.setPassword(passwordEncoder.encode(client.password()));
        user.setRole(UserRole.CLIENT);
        return user;
    }
}
