package com.eduardo.geolocation_h3uber.listeners;

import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.events.AddressCreatedEvent;
import com.eduardo.geolocation_h3uber.repositories.AddressRepository;
import com.uber.h3core.H3Core;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoLocationListener {

    private final AddressRepository addressRepository;
    private final H3Core h3Core;

    private static final int H3_RESOLUTION = 6;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAddressCreatedEvent(AddressCreatedEvent event) {
        log.info("Iniciando calculdo H3 em background para o endereco ID: {}", event.addressId());

        try {
            String h3Index = h3Core.latLngToCellAddress(
                    event.latitude(),
                    event.longitude(),
                    H3_RESOLUTION
            );

            AddressEntity address = addressRepository.findById(event.addressId())
                    .orElseThrow(() -> new RuntimeException("Endereço não encontrado para o ID: " + event.addressId()));
            address.setH3Index(h3Index);
            addressRepository.save(address);
        } catch (Exception e) {
            log.error("Falha ao processar geolocalização para o endereço ID: {}", event.addressId(), e);
        }
    }
}
