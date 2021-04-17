package org.seng302.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;

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

@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = TestApplication.class)
@RunWith(SpringRunner.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private ProductController productController;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    private ObjectMapper mapper;

    private User user;
    private Business business;
    private Product product1;
    private Product product2;

    private NewProductRequest productUpdate;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        user = new User("testemail@email.com", "testpassword", Role.USER);
        user.setId(1L);

        Address businessAddress = new Address(null, null, null, null, "New Zealand", null);
        business = new Business("TestBusiness", "Test Description", businessAddress, BusinessType.RETAIL_TRADE);
        business.createBusiness(user);
        business.setId(1L);

        product1 = new Product("07-4957066", 1, "Spoon", "Soup, Plastic", 14.69, new Date());
        product2 = new Product("07-4957066", 1, "Seedlings", "Buckwheat, Organic", 1.26, new Date());

        //Mocking body of PUT request
        productUpdate = new NewProductRequest("replace id", "replace name", "replace desc", 2.2);
    }

    @Test
    public void testNoAuthGetBusinessProductCatalogue() throws Exception {
        mvc.perform(get("/businesses/{id}/products", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulGetBusinessProductCatalogue() throws Exception {
        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductsByBusinessId(business.getId())).thenReturn(productList);

        MvcResult result = mvc.perform(get("/businesses/{id}/products", business.getId())
                    .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                    .andReturn();

        // Test for 200 status & correct JSON output.
        assert result.getResponse().getStatus() == HttpStatus.OK.value();
        assertThat(result.getResponse().getContentAsString()).isEqualToIgnoringWhitespace(mapper.writeValueAsString(productList));

        // Change to a Global Application Admin user.
        user.setId(99);
        user.setRole(Role.GAA);
        mvc.perform(get("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testBadGetBusinessProductCatalogue() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(null);
        mvc.perform(get("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());

        User nonAdminUser = new User("email@email.com", "password", Role.USER);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);

        mvc.perform(get("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, nonAdminUser))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testNoAuthPostProduct() throws Exception {
        mvc.perform(post("/businesses/{id}/products", business.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testForbiddenUserPostProduct() throws Exception {
        User forbiddenUser = new User("email@email.com", "password", Role.USER);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, forbiddenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostProductAsGlobalAdmin() throws Exception {
        User DGAAUser = new User("email@email.com", "password", Role.DGAA);
        User GAAUser = new User("email2@email.com", "password", Role.GAA);

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, DGAAUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, GAAUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulPostProductAsBusinessAdmin() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());

        User businessSecondaryAdmin = new User("email@email.com", "password", Role.USER);
        business.getAdministrators().add(businessSecondaryAdmin);
        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, businessSecondaryAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostProductDuplicateId() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(product1);

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());

    }



    @Test
    @WithMockUser(roles="USER")
    public void testPostProductEmptyId() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        product1.setId(null);

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());

        product1.setId("");

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostProductEmptyName() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        product1.setName(null);

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());

        product1.setName("");

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostProductEmptyDescription() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        product1.setDescription(null);

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());

        product1.setDescription("");

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPostProductNegativePrice() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        product1.setRecommendedRetailPrice(-0.01);

        mvc.perform(post("/businesses/{id}/products", business.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNoAuthPutProduct() throws Exception {
        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId()))
                .andExpect(status().isUnauthorized());
    }

    //PUT (Modify Catalogue Entries) tests, similar to POST tests since they're both very similar
    @Test
    @WithMockUser(roles="USER")
    public void testForbiddenUserPutProduct() throws Exception {
        User forbiddenUser = new User("email@email.com", "password", Role.USER);
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(product1);
        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, forbiddenUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles="USER")
    public void testPutProductAsGlobalAdmin() throws Exception {
        User DGAAUser = new User("email@email.com", "password", Role.DGAA);
        User GAAUser = new User("email2@email.com", "password", Role.GAA);

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(product1);

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, DGAAUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isOk());

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, GAAUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulPutProductAsBusinessAdmin() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(product1);

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isOk());

        User businessSecondaryAdmin = new User("email@email.com", "password", Role.USER);
        business.getAdministrators().add(businessSecondaryAdmin);
        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, businessSecondaryAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles="USER")
    public void testPutProductEmptyId() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        productUpdate.setId(null);

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());

        productUpdate.setId("");

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPutProductEmptyName() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        productUpdate.setName(null);

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());

        productUpdate.setName("");

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPutProductEmptyDescription() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        productUpdate.setDescription(null);

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());

        productUpdate.setDescription("");

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testPutProductNegativePrice() throws Exception {
        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(null);
        productUpdate.setRecommendedRetailPrice(-0.01);

        mvc.perform(put("/businesses/{businessId}/products/{productId}", business.getId(), product1.getId())
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(productUpdate)))
                .andExpect(status().isBadRequest());
    }



    // Adding Product Image Tests.
    @Test
    public void testNoAuthAddProductImage() throws Exception {
        mvc.perform(post("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testSuccessfulAddProductImage() throws Exception {
        File data = ResourceUtils.getFile("src/test/resources/media/images/testlettuce.jpeg");
        assertThat(data).exists();

        byte[] bytes = FileCopyUtils.copyToByteArray(data);
        MockMultipartFile image = new MockMultipartFile("filename", "test.jpg", MediaType.IMAGE_JPEG_VALUE, bytes);

        Mockito.when(businessRepository.findBusinessById(business.getId())).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(product1);

        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId())
                .file(image)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isCreated());

        User gaaUser = new User("fake@fakemail.com", "testpass", Role.GAA);

        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId())
                .file(image)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, gaaUser))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(roles="USER")
    public void testBadPathForProductImages() throws Exception {
        File data = ResourceUtils.getFile("src/test/resources/media/images/testlettuce.jpeg");
        assertThat(data).exists();
        byte[] bytes = FileCopyUtils.copyToByteArray(data);
        MockMultipartFile image = new MockMultipartFile("filename", "test.jpg", MediaType.IMAGE_JPEG_VALUE, bytes);

        // Non-existent business.
        Mockito.when(businessRepository.findBusinessById(5)).thenReturn(null);
        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId())
                .file(image)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());

        // Non-existent product.
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        Mockito.when(productRepository.findProductByIdAndBusinessId("FAKE-123", business.getId())).thenReturn(null);
        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), "FAKE-123")
                .file(image)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(roles="USER")
    public void testUnsuccessfulAddProductImage() throws Exception {
        // No Image Supplied
        Mockito.when(businessRepository.findBusinessById(1)).thenReturn(business);
        MockMultipartFile noImageFile = new MockMultipartFile("filename", null, null, (byte[]) null);
        Mockito.when(productRepository.findProductByIdAndBusinessId(product1.getId(), business.getId())).thenReturn(product1);
        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId())
                .file(noImageFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isBadRequest());

        // Incorrect file type supplied.
        byte[] badTypeBytes = "Hello World".getBytes();
        MockMultipartFile badTypeFile = new MockMultipartFile("filename", "", MediaType.TEXT_HTML_VALUE, badTypeBytes);
        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId())
                .file(badTypeFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, user))
                .andExpect(status().isBadRequest());

        // Forbidden Account
        User anotherUser = new User("fake@fakemail.com", "testpass", Role.USER);
        mvc.perform(multipart("/businesses/{businessId}/products/{productId}/images", business.getId(), product1.getId())
                .file(badTypeFile)
                .sessionAttr(User.USER_SESSION_ATTRIBUTE, anotherUser))
                .andExpect(status().isForbidden());

    }


}
