package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Data
@Entity
@IdClass(ProductId.class)
public class Product {

    @Id
    private String id;
    @JsonIgnore
    @Id
    private long businessId;

    private String name;
    private String description;
    private double recommendedRetailPrice;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date created;
    // images when we get to it.

    protected Product() { }

    public Product(String id, long businessId, String name, String description, double recommendedRetailPrice, Date created) {
        this.id = id;
        this.businessId = businessId;
        this.name = name;
        this.description = description;
        this.recommendedRetailPrice = recommendedRetailPrice;
        this.created = created;
    }

}
