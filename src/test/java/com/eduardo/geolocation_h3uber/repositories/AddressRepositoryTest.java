package com.eduardo.geolocation_h3uber.repositories;

import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.utils.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AddressRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("Should save and find address by id")
    void shouldSaveAndFindAddressById() {
        AddressEntity address = new AddressEntity();
        address.setLogradouro("Rua A");
        address.setNumero("123");
        address.setLatitude(-23.55052);
        address.setLongitude(-46.633308);
        address.setH3Index("882b304b11fffff");

        AddressEntity savedAddress = addressRepository.save(address);

        Optional<AddressEntity> foundAddress = addressRepository.findById(savedAddress.getId());

        assertThat(foundAddress).isPresent();
        assertThat(foundAddress.get().getLogradouro()).isEqualTo("Rua A");
        assertThat(foundAddress.get().getH3Index()).isEqualTo("882b304b11fffff");
    }
}
