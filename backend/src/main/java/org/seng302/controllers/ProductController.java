package org.seng302.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.exceptions.InvalidImageExtensionException;
import org.seng302.models.*;
import org.seng302.models.requests.NewProductRequest;
import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller with product related endpoints.
 */
@RestController
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class.getName());

    @Autowired private ProductRepository productRepository;
    @Autowired private BusinessRepository businessRepository;

    @Value("${media.image.business.directory}")
    String rootImageDir;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retrieves all of the products in the business' product catalogue.
     * Authentication required endpoint - 401 if unauthorized.
     * @param id unique identifier of the business.
     * @return an (potentially empty) array of products that the business owns.
     */
    @GetMapping("/businesses/{id}/products")
    public ResponseEntity<List<Product>> getBusinessProductCatalogue(@PathVariable long id, HttpSession session) {
        Business business = businessRepository.findBusinessById(id);
        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Product> products = productRepository.findProductsByBusinessId(id);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }


    /**
     * Creates a new product and adds it to the product catalogue of the current acting business
     * Authentication is required, user must be a business admin or a default global admin
     * @param id unique identifier of the business
     * @param req the request body for the new product object
     * @param session http session which holds the authenticated user
     * @return error codes: 403 (forbidden user), 400 (bad request for product), 201 (object valid and created)
     * @throws JsonProcessingException
     */
    @PostMapping("/businesses/{id}/products")
    public ResponseEntity<String> createProduct(@PathVariable long id, @RequestBody NewProductRequest req, HttpSession session) {
        Business business = businessRepository.findBusinessById(id);

        if (business == null) { // Business does not exist
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            ArrayList adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
            User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

            if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) { // User is not authorized to add products
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else { // User is authorized
                ArrayList checkProduct = isValidProduct(req, business);
                boolean isValid = (Boolean) checkProduct.get(0);
                String errorMessage = (String) checkProduct.get(1);
                if (isValid) {
                    Product newProduct = new Product(req, business.getId());
                    productRepository.save(newProduct);
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
            }
        }
    }

    /**
     * Checks the product request body to ensure all fields are valid
     * @param product The product to be created, includes id, name, description and price
     * @param business The business that is creating the product
     * @return True if all fields are valid, otherwise false
     */
    private ArrayList<Object> isValidProduct(NewProductRequest product, Business business) {
        boolean isValid = true;
        String errorMessage = null;

        if (productRepository.findProductByIdAndBusinessId(product.getId(), business.getId()) != null) {
            errorMessage = "A product already exists with this ID";
            isValid = false;
        } else if (product.getId() == null || product.getId() == "") {
            errorMessage = "Product id can not be empty";
            isValid = false;
        } else if (product.getName() == null || product.getName() == "") {
            errorMessage = "Product name can not be empty";
            isValid = false;
        } else if (product.getDescription() == null || product.getDescription() == "") {
            errorMessage = "Product description can not be empty";
            isValid = false;
        } else if (product.getRecommendedRetailPrice() == null || product.getRecommendedRetailPrice() < 0) {
            errorMessage = "Product recommended retail price must be at least 0";
            isValid = false;
        }

        ArrayList returnObjects = new ArrayList();
        returnObjects.add(isValid);
        returnObjects.add(errorMessage);
        return returnObjects;
    }

    /**
     * Receives and saves a new image pertaining to a product.
     * Authorization required - 401 code response if not.
     * @param businessId unique identifier of the business that the image is relating to.
     * @param productId product identifier that the image is relating to.
     * @param image a multipart image of the file
     * @return ResponseEntity with the appriate status codes - 201, 400, 403, 406.
     * @throws IOException Thrown when file writing fails.
     */
    @PostMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<String> addProductImage(@PathVariable long businessId, @PathVariable String productId, @RequestPart(name="filename") MultipartFile image, HttpServletRequest request, HttpSession session) throws Exception {
        Business business = businessRepository.findBusinessById(businessId);
        if (business == null)  {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        Product product = productRepository.findProductByIdAndBusinessId(productId, businessId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        User user = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);
        if (!business.collectAdministratorIds().contains(user.getId()) && !Role.isGlobalApplicationAdmin(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String imageExtension;
        if (image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No image supplied.");
        }
        try { // Throw error if the file is not an image.
            imageExtension = Image.getContentTypeExtension(image.getContentType());
        }
        catch (InvalidImageExtensionException exception) {
            throw new InvalidImageExtensionException(exception.getMessage());
        }

        // Check if business' own folder directory exists - make directory if false.
        File businessDir = new File(rootImageDir + "business_" + businessId);
        if (businessDir.mkdir()) {
            logger.info("Image of business directory did not exist - new directory created of " + businessDir.getPath());
        }

        String imageName = "";
        boolean freeImage = false;
        int count = 0;
        while (!freeImage) {
            imageName = String.valueOf(count);
            File checkFile = new File(businessDir + "/" + imageName + imageExtension);

            if (checkFile.exists()) {
                count++;
            }
            else {
                freeImage = true;
            }
        }
        File file = new File(businessDir + "/" + imageName + imageExtension);
        logger.info(System.getProperty("user.dir"));
        logger.info("File Being written into: " + file);
        file.createNewFile();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream((file)));
        stream.write(image.getBytes());
        stream.close();

        // Save into DB.
        Image newImage = new Image("business_" + businessId + imageName + imageExtension, null);
        product.addProductImage(newImage);
        productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
         * deletes an image
         * @param businessId unique identifier of the business that the image is relating to.
         * @param productId product identifier that the image is relating to.
         * @param image a multipart image of the file
         * @return
         * @throws IOException
         */
    @DeleteMapping("/businesses/{businessId}/products/{productId}/images")
    public ResponseEntity<String> deleteProductImage(@PathVariable long businessId, @PathVariable String productId, @RequestPart(name="filename") MultipartFile image ) throws Exception {
        return null;
    }


}
