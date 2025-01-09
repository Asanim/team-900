package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.TestApplication;
import org.seng302.models.Address;
import org.seng302.models.Card;
import org.seng302.models.MarketplaceSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests of the user repository.
 */

@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
public class CardRepositoryTests {

    @Autowired
    private CardRepository cardRepository;


    @BeforeEach
    void setUp() {
        assertThat(cardRepository).isNotNull();
        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
        Address a2 = new Address("620","Sutherland Lane","Dalai", null,"China", null);

        cardRepository.save();
    }

    // Currently broken in gitlab-runner.
//    @Test
//    public void findBusiness() {
//        Business found = businessRepository.findBusinessById(1);
//
//        assertThat(found.getName()).isEqualTo("Business1");
//        assertThat(found.getBusinessType()).isEqualTo(BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
//
//        Business notFound = businessRepository.findBusinessById(100);
//        assertThat(notFound).isNull();
//
//    }


}
