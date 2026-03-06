package com.eduardo.geolocation_h3uber.repositories;

import com.eduardo.geolocation_h3uber.entities.AddressEntity;
import com.eduardo.geolocation_h3uber.entities.CompanyEntity;
import com.eduardo.geolocation_h3uber.utils.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CompanyRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("Should save and find company by id")
    void shouldSaveAndFindCompanyById() {
        CompanyEntity company = new CompanyEntity();
        company.setName("Tech Corp");

        CompanyEntity savedCompany = companyRepository.save(company);

        Optional<CompanyEntity> foundCompany = companyRepository.findById(savedCompany.getId());

        assertThat(foundCompany).isPresent();
        assertThat(foundCompany.get().getName()).isEqualTo("Tech Corp");
    }

    @Test
    @DisplayName("Should find all companies by H3 indexes")
    void shouldFindAllByH3IndexIn() {
        CompanyEntity company1 = new CompanyEntity();
        company1.setName("Company 1");
        company1 = companyRepository.save(company1);

        AddressEntity address1 = new AddressEntity();
        address1.setH3Index("882b304b11fffff");
        address1.setCompany(company1);
        addressRepository.save(address1);

        CompanyEntity company2 = new CompanyEntity();
        company2.setName("Company 2");
        company2 = companyRepository.save(company2);

        AddressEntity address2 = new AddressEntity();
        address2.setH3Index("882b304b13fffff");
        address2.setCompany(company2);
        addressRepository.save(address2);

        List<String> h3Neighbors = List.of("882b304b11fffff", "other-index");

        List<CompanyEntity> companies = companyRepository.findAllByH3IndexIn(h3Neighbors);

        assertThat(companies).hasSize(1);
        assertThat(companies.getFirst().getName()).isEqualTo("Company 1");
    }
}
