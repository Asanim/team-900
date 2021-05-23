package org.seng302.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.seng302.models.*;
import org.seng302.models.requests.NewInventoryRequest;

import org.seng302.repositories.BusinessRepository;
import org.seng302.repositories.InventoryRepository;
import org.seng302.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InventoryController {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get request mapping for getting business inventory by business id
     * @param id the business's id
     * @return ResponseEntity
     * @throws JsonProcessingException when json mapping object to a json string fails unexpectedly.
     */
    @GetMapping("/businesses/{id}/inventory")
    public ResponseEntity<List<Inventory>> getInventory(@PathVariable String id, HttpSession session) throws JsonProcessingException {
        Business business = businessRepository.findBusinessById(Long.parseLong(id));
        User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        ArrayList adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));

        if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Inventory> inventoryItems = inventoryRepository.findInventoryByBusinessId(business.getId());
        return ResponseEntity.status(HttpStatus.OK).body(inventoryItems);
    }


    /**
     * POST request for inventory
     * @param id the business's id
     * @param request incoming JSON request containing inventory information
     * @return ResponseEntity
     * @throws JsonProcessingException when json mapping object to a json string fails unexpectedly.
     */
    @PostMapping("/businesses/{id}/inventory")
    public ResponseEntity<List<Product>> postInventory(@PathVariable Long id, @RequestBody NewInventoryRequest request, HttpSession session) {
        Business business = businessRepository.findBusinessById(id);

        Product product = productRepository.findProductByIdAndBusinessId(request.getProductId(), business.getId());

        if(product == null) { //Product doesn't exist in business
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(business == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else {
            ArrayList adminIds = business.getAdministrators().stream().map(User::getId).collect(Collectors.toCollection(ArrayList::new));
            User currentUser = (User) session.getAttribute(User.USER_SESSION_ATTRIBUTE);

            if (!(adminIds.contains(currentUser.getId()) || Role.isGlobalApplicationAdmin(currentUser.getRole()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                try { //Validation done in Inventory model
                    Inventory newInventoryItem = new Inventory(request, business.getId());
                    inventoryRepository.save(newInventoryItem);

                    return ResponseEntity.status(HttpStatus.CREATED).build();
                } catch(ValidationException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            }
        }


    }
}