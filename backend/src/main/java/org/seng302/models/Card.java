package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
//import org.seng302.models.requests.NewCardRequest;

import javax.xml.bind.ValidationException;
import javax.persistence.*;
import java.util.Date;
import java.util.Calendar;
import org.seng302.models.*;
import java.util.List;
import org.seng302.models.Address;

/**
 * Entity class for a user created card
 *
 * Rather than include the user object itself, I have chosen only take their user name and address.
 * Possible information leak as when we send the card list to the front end, we sent the json object in it's entirety.
 * ie when including the user, it will also include every other field about them
 * we want only reveal the information required rather than expose a possibe security risk.
 */
@Data
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String userName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address userHomeAddress; //only city and suburb...

    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    private String keywords; //comma delimited? Or use a List?

    @JsonIgnore
    @Column(name = "card_type")
    private MarketplaceSection cardType; //ForSale, wanted, exchange

    /**
     * Constructor for a new card object
     * @param userName
     * @param userHomeAddress
     * @param title
     * @param description
     * @param created
     * @param keywords
     * @param cardType
     */
    public Card(String userName, Address userHomeAddress, String title, String description, Date created, String keywords, MarketplaceSection cardType ) {
        this.userName = userName;
        this.userHomeAddress = userHomeAddress;
        this.title = title;
        this.description = description;
        this.created = created;
        this.keywords = keywords;
        this.cardType = cardType;
    }


    /**
     * Empty constructor for JPA use.
     */
    protected Card() {
    }


    /**
     * Used for when a new card request is called.
     *
     * @param inventoryItem     the inventory item object that is being listed for sale
     * @param newListingRequest The request body information that was mapped into a newListingRequest.
     */
    /*
    public Listing(Inventory inventoryItem, NewListingRequest newListingRequest) throws ValidationException {
        if (this.validListingRequest(inventoryItem, newListingRequest)) {
            this.inventoryItem = inventoryItem;
            this.quantity = newListingRequest.getQuantity();
            this.price = newListingRequest.getPrice();
            this.moreInfo = newListingRequest.getMoreInfo();
            this.created = new Date();
            this.closes = newListingRequest.getCloses();
        } else {
            throw new ValidationException("Listing request item is not valid!");
        }
    }

    private boolean validListingRequest(Inventory inventoryItem, NewListingRequest req) {
        Date today = Calendar.getInstance().getTime();
        if (inventoryItem == null) {
            return false;
        } else {
            int quantityOfInventory = inventoryItem.getQuantity();
            if (req.getQuantity() < 1 || req.getPrice() < 0) {
                return false;
            } else if (req.getCloses() == null || req.getCloses().before(today)) {
                return false;
            } else if (req.getQuantity() > quantityOfInventory) {
                return false;
            }
            return true;
        }

    }
*/

}
