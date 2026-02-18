package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.uber.h3core.H3Core;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final H3Core h3;

    @Value("${h3.resolution:9}")
    private int resolution;

    public void processarGeolocalizacao(AddressEntity address) {
        // RF10: Transformar endereço em lat/long
        // Aqui simulamos uma chamada a API (ex: Google Maps ou Nominatim)
        if (address.getLatitude() == null || address.getLongitude() == null) {
            // Mock de coordenadas para o seu projeto de aprendizado
            address.setLatitude(-25.4297);
            address.setLongitude(-49.2719);
        }

        // H3: Converte coordenadas para o índice Hexagonal
        String h3Index = h3.latLngToCellAddress(
                address.getLatitude(),
                address.getLongitude(),
                resolution
        );

        address.setH3Index(h3Index);
    }

    public List<String> calcularVizinhos(String h3Index, int raio) {
        // Retorna o índice central + os hexágonos ao redor (K-ring)
        return h3.gridDisk(h3Index, raio);
    }
}
