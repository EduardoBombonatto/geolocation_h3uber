package com.eduardo.geolocation_h3uber.repositories;

import com.eduardo.geolocation_h3uber.entities.ClientEntity;
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
class ClientRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("Should save and find client by id")
    void shouldSaveAndFindClientById() {
        ClientEntity client = new ClientEntity();
        client.setName("Eduardo");
        client.setEmail("eduardo@example.com");

        ClientEntity savedClient = clientRepository.save(client);

        Optional<ClientEntity> foundClient = clientRepository.findById(savedClient.getId());

        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getName()).isEqualTo("Eduardo");
        assertThat(foundClient.get().getEmail()).isEqualTo("eduardo@example.com");
    }
}
