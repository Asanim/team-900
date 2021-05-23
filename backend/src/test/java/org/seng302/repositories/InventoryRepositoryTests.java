package org.seng302.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.TestApplication;
import org.seng302.models.Product;
import org.seng302.models.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ContextConfiguration(classes = TestApplication.class)
@DataJpaTest
public class InventoryRepositoryTests {

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory testInven1;
    private Inventory testInven2;

    @BeforeEach
    void setUp() {
        assertThat(inventoryRepository).isNotNull();
        testInven1 = new Inventory("07-4957066", 1, 5, 2.0, 10.0, new Date("2021-01-28"), new Date("2021-04-28"), new Date("2021-05-28"), new Date("2021-06-28"));
        testInven2 = new Inventory("55-9986232", 2, 7, 4.0, 28.0, new Date("2021-01-28"), new Date("2021-04-28"), new Date("2021-05-28"), new Date("2021-06-28"));

        inventoryRepository.save(testInven1);
        inventoryRepository.save(testInven2);
    }

    //@Test
    public void saveInventory() {
        Inventory inventory = new Inventory("12-5088639", 1, 4, 4.0, 16.0, new Date("2021-01-28"), new Date("2021-04-28"), new Date("2021-05-28"), new Date("2021-06-28"));
        inventoryRepository.save(inventory);

        List<Inventory> inventorys = inventoryRepository.findInventoryByBusinessId(1);
        assertThat(inventorys.size()).isEqualTo(2);
        Inventory anotherInventory = new Inventory("13-5699", 1, 2, 3.0, 6.0, new Date("2021-01-28"), new Date("2021-04-28"), new Date("2021-05-28"), new Date("2021-06-28"));
        inventoryRepository.save(anotherInventory);

        inventorys = inventoryRepository.findInventoryByBusinessId(1);
        assertThat(inventorys.size()).isEqualTo(3);

        // Inserting the same product id and business id.
        inventoryRepository.save(anotherInventory);
        inventorys = inventoryRepository.findInventoryByBusinessId(1);
        assertThat(inventorys.size()).isEqualTo(3);
    }
}
