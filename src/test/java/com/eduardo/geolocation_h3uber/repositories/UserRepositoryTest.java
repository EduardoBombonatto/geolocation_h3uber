package com.eduardo.geolocation_h3uber.repositories;

import com.eduardo.geolocation_h3uber.entities.UserEntity;
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
class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and find user by id")
    void shouldSaveAndFindUserById() {
        UserEntity user = new UserEntity();
        user.setName("Eduardo");
        user.setEmail("eduardo@example.com");

        UserEntity savedUser = userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Eduardo");
        assertThat(foundUser.get().getEmail()).isEqualTo("eduardo@example.com");
    }
}
