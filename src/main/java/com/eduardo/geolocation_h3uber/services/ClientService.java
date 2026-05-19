package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.dtos.CompanyDTO;
import com.eduardo.geolocation_h3uber.dtos.ClientDTO;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.entities.ClientEntity;
import com.eduardo.geolocation_h3uber.exceptions.AddressRequiredException;
import com.eduardo.geolocation_h3uber.exceptions.H3IndexNotFoundException;
import com.eduardo.geolocation_h3uber.exceptions.ClientNotFoundException;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Transactional
    public ClientDTO createClient(ClientDTO clientDTO) {
        ClientEntity clientEntity = modelMapper.map(clientDTO, ClientEntity.class);

        validateAddress(clientEntity);

        clientEntity.getAddress().setClient(clientEntity);
        ClientEntity savedClient = clientRepository.save(clientEntity);

        addressEventService.publishAddressCreatedEvent(savedClient.getAddress());

        return modelMapper.map(savedClient, ClientDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> findNearbyCompanies(UUID clientId, int radiusInHexagons) {
        ClientEntity client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Usuário não encontrado"));

        String clientH3Index = getClientH3Index(client);

        List<String> neighbors = geolocationService.findNeighbors(clientH3Index, radiusInHexagons);

        List<CompanyEntity> nearbyCompanies = companyRepository.findAllByH3IndexIn(neighbors);

        return nearbyCompanies.stream()
                .map(company -> modelMapper.map(company, CompanyDTO.class))
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
}
