package org.seng302.models.requests;

import lombok.Data;
import java.util.Date;

/**
 * DTO class that holds inventory info when added a product to it.
 */
@Data
public class NewInventoryRequest {

    private String productId;
    private int quantity;
    private double pricePerItem;
    private double totalPrice;
    private Date manufactured;
    private Date sellBy;
    private Date bestBefore;
    private Date expires;

    /**
     * New inventory Request. Called when the a new inventory listing is added from the frontend.
     * @param productId
     * @param quantity
     * @param pricePerItem
     * @param totalPrice
     * @param manufactured
     * @param sellBy
     * @param bestBefore
     * @param expires
     */
    public NewInventoryRequest(String productId, int quantity, double pricePerItem, double totalPrice, Date manufactured, Date sellBy, Date bestBefore, Date expires) {
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.totalPrice = totalPrice;
        this.manufactured = manufactured;
        this.sellBy = sellBy;
        this.bestBefore = bestBefore;
        this.manufactured = manufactured;
        this.expires = expires;
    }
}
