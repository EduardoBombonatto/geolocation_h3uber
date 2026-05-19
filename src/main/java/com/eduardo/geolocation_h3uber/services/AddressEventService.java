package com.eduardo.geolocation_h3uber.services;

import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.events.AddressCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressEventService {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Publishes an AddressCreatedEvent if the address has valid coordinates.
     *
     * @param address The address entity.
     */
    public void publishAddressCreatedEvent(AddressEntity address) {
        if (address != null && address.getLatitude() != null && address.getLongitude() != null) {
            eventPublisher.publishEvent(new AddressCreatedEvent(
                    address.getId(),
                    address.getLatitude(),
                    address.getLongitude()
            ));
        }
    }
}
