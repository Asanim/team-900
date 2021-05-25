package org.seng302.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.ca.Cal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.seng302.TestApplication;
import org.seng302.models.*;
import org.seng302.models.requests.NewListingRequest;
import org.seng302.models.requests.UserIdRequest;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.InventoryRepository;
import org.seng302.repositories.ListingRepository;
import org.seng302.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.*;

@WebMvcTest(controllers = ListingController.class)
@ContextConfiguration(classes = TestApplication.class)

public class ListingControllerTest {

    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private ListingController listingController;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private InventoryRepository inventoryRepository;
    @MockBean
    private ListingRepository listingRepository;
    @Autowired
    private ObjectMapper mapper;

    private User ownerUser;
    private User adminUser;
    private User user;
    private Business business;
    private Product product1;
    private Inventory inventory1;
    private Listing listing1;
    private NewListingRequest newListingRequest;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        ownerUser = new User("Rayna", "YEP", "Dalgety", "Universal", "zero tolerance task-force" , "rdalgety3@ocn.ne.jp","2006-03-30","+7 684 622 5902",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ATQWJM");
        ownerUser.setId(1L);
        user = new User("Elwood", "YEP", "Altamirano", "Visionary", "mobile capacity", "ealtamirano8@phpbb.com","1927-02-28","+381 643 240 6530",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"ItqVNvM2JBA");
        user.setId(2L);
        adminUser = new User("Gannon", "YEP", "Tynemouth", "Exclusive", "6th generation intranet", "gtynemouth1@indiatimes.com","1996-03-31","+62 140 282 1784",new Address("32", "Little Fleur Trail", "Christchurch" ,"Canterbury", "New Zealand", "8080"),"HGD0nAJNjSD");
        adminUser.setId(3L);

        Address a1 = new Address("1","Kropf Court","Jequitinhonha", null, "Brazil","39960-000");
        business = new Business("Business1", "Test Business 1", a1, BusinessType.ACCOMMODATION_AND_FOOD_SERVICES);
        business.setId(1L);
        business.createBusiness(ownerUser);
        business.getAdministrators().add(adminUser);

        product1 = new Product("07-4957066", 1, "Spoon", "Soup, Plastic", "Good Manufacturer", 14.69, new Date());

        Calendar afterCalendar = Calendar.getInstance();
        afterCalendar.set(2022, 1, 1);
        Date laterDate = afterCalendar.getTime();

        Calendar beforeCalendar = Calendar.getInstance();
        afterCalendar.set(2020, 1, 1);
        Date beforeDate = beforeCalendar.getTime();

        inventory1 = new Inventory("07-4957066", 1, 10, 2.0, 20.0, beforeDate, laterDate, laterDate, laterDate);
        newListingRequest = new NewListingRequest(inventory1.getId(), 2, 2.99, "more info", laterDate);
        listing1 = new Listing(inventory1, 5, 2.0, "Seller may be interested in offers", new Date(), laterDate);
        listingRepository.save(listing1);
        assertThat(business.getAdministrators().size()).isEqualTo(2);
    }

    //Get listings tests

    @Test
    public void testNoAuthGetListing() throws Exception {
        mvc.perform(get("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulGetBusinessListings() throws Exception {
        List<Listing> listingList = new ArrayList<>();
        listingList.add(listing1);

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(listingRepository.findListingsByInventoryItem(inventory1)).thenReturn(listingList);

        mvc.perform(get("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testGetListingForNullBusiness() throws Exception {
        User user = new User("email@email.com", "password", Role.USER);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(null);
        mvc.perform(get("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());
    }

    //
    ////Inventory POST tests
    //

    @Test
    public void testNoAuthPostListing() throws Exception {
        mvc.perform(post("/businesses/{id}/listings", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testForbiddenUserPostListing() throws Exception {
        User forbiddenUser = new User("email@email.com", "password", Role.USER);

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        //Have as param in here since the request object is null in the test
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, forbiddenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newListingRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostListingAsGlobalAdmin() throws Exception {
        User DGAAUser = new User("email@email.com", "password", Role.DGAA);
        User GAAUser = new User("email2@email.com", "password", Role.GAA);

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, DGAAUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newListingRequest)))
                .andExpect(status().isCreated());

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, GAAUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newListingRequest)))
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulPostListingAsBusinessAdmin() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newListingRequest)))
                .andExpect(status().isCreated());

        User businessSecondaryAdmin = new User("email@email.com", "password", Role.USER);
        business.getAdministrators().add(businessSecondaryAdmin);
        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, businessSecondaryAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newListingRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostWithNoQuantity() throws Exception {
        listing1.setQuantity(0);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(listing1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostWithQuantityLargerThanInventoryQuantity() throws Exception {
        //inventory quantity is 10
        inventory1.setQuantity(4);
        listing1.setQuantity(8);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(listing1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostWithNegativeQuantity() throws Exception {
        //inventory quantity is 10
        listing1.setQuantity(-2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(listing1)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostWithQuantityEqualToInventoryQuantity() throws Exception {
        //inventory quantity is 10
        inventory1.setQuantity(8);
        newListingRequest.setQuantity(8);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newListingRequest)))
                .andExpect(status().isCreated());

    }


    @Test
    @WithMockUser(roles="USER")
    public void testPostWithNegativePrice() throws Exception {
        listing1.setPrice(-2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(listing1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostWithClosingDateInPast() throws Exception {
        Calendar pastDate = Calendar.getInstance();
        pastDate.set(2020, 1, 1);
        Date laterDate = pastDate.getTime();

        listing1.setCloses(laterDate);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(null, business.getId())).thenReturn(product1);
        Mockito.when(inventoryRepository.findInventoryById(inventory1.getId())).thenReturn(inventory1);

        mvc.perform(post("/businesses/{id}/listings", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, adminUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(listing1)))
                .andExpect(status().isBadRequest());
    }

}
