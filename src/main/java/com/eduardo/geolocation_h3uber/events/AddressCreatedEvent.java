package com.eduardo.geolocation_h3uber.events;

/**
 * Evento disparado logo após um endereço ser salvo no banco de dados.
 * Contém apenas os dados essenciais para o cálculo assíncrono do H3.
 */
public record AddressCreatedEvent(
        Long addressId,
        Double latitude,
        Double longitude
) {
}
