package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.repositories.AddressRepository;
import com.eduardo.geolocation_h3uber.repositories.CompanyRepository;
import com.eduardo.geolocation_h3uber.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

    @Transactional
    public CompanyEntity saveCompnay(CompanyEntity company) {
        if (company.getAddress() != null){
            addressService.processarGeolocalizacao(company.getAddress());
            company.getAddress().setCompany(company);
        }
        return companyRepository.save(company);
    }

    public List<CompanyEntity> buscarPertoDoUsuario(UUID usuarioId) {
        var usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String userH3 = usuario.getAddress().getH3Index();

        // Define um raio de 1 ou 2 hexágonos de distância
        List<String> vizinhos = addressService.calcularVizinhos(userH3, 2);

        return companyRepository.findAllByH3IndexIN(vizinhos);
    }
}
