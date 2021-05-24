package org.seng302.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.ca.Cal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.models.requests.NewProductRequest;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.ProductRepository;
import org.seng302.repositories.InventoryRepository;
import org.seng302.repositories.ListingRepository;
import org.seng302.utilities.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;


@WebMvcTest(controllers = ListingController.class)
@ContextConfiguration(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class ListingControllerTests {

    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private ProductController productController;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private InventoryRepository inventoryRepository;
    @MockBean
    private ListingRepository listingRepository;
    @MockBean
    private FileService fileService;
    @Autowired
    private ObjectMapper mapper;

    private User user;
    private Business business;
    private Inventory testInven1;
    private Inventory testInven2;
    private Listing testListing1;

    private NewProductRequest productUpdate;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        Calendar calendar = Calendar.getInstance();
        Date dateCreated = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date dateCloses = calendar.getTime();


        user = new User("testemail@email.com", "testpassword", Role.USER);
        user.setId(1L);

        Address businessAddress = new Address(null, null, null, null, "New Zealand", null);
        business = new Business("TestBusiness", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        business.createBusiness(user);
        business.setId(1L);

        Product testProd1 = new Product("77-9986231", 1L, "Seedlings", "Buckwheat, Organic", "Nestle", 1.26, new Date());
        Product testProd2 = new Product("77-5088639", 1L, "Foam Cup", "6 Oz", "Edible Objects Ltd.",55.2, new Date());

        productRepository.save(testProd1);
        productRepository.save(testProd2);

        testInven1 = new Inventory("77-9986231", 1L, 5, 2.0, 10.0, dateCreated,dateCloses, dateCloses, dateCloses);
        testInven2 = new Inventory("77-5088639", 1L, 7, 4.0, 28.0, dateCreated,dateCloses, dateCloses, dateCloses);

        inventoryRepository.save(testInven1);
        inventoryRepository.save(testInven2);

        //test Data for Listings
        testListing1 = new Listing(testInven1, 12, 12.00, "test more info", dateCreated, dateCloses);

        listingRepository.save(testListing1);
    }

    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulGetBusinessListings() throws Exception {
        List<Listing> listingList = new ArrayList<>();
        listingList.add(testListing1);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(listingRepository.findListingsByInventoryItem(testInven1)).thenReturn(listingList);

        MvcResult result = mvc.perform(get("/businesses/{id}/listings", business.getId())
                    .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                    .andReturn();

        // Test for 200 status & correct JSON output.
        assert result.getResponse().getStatus() == HttpStatus.OK.value();

        //System.out.println("DEBUG: " + result.getResponse().getContentAsString());
        //assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(mapper.writeValueAsString(listingList));
    }

    @Test
    public void testForbiddenBusinessGetBusinessListings() throws Exception {
        int forbiddenBusinessID = 99999;
        mvc.perform(get("/businesses/{id}/products", forbiddenBusinessID))
                .andExpect(status().isUnauthorized());
    }
}
