package org.seng302.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Getter @Setter // generate setters and getters for all fields (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(name = "BUSINESS_ADMINS",
                    joinColumns = @JoinColumn(name="BUSINESS_ID"),
                    inverseJoinColumns = @JoinColumn(name="USER_ID"))
    private Set<User> administrators = new HashSet<>();
    private String name;
    private String description;
    private String address;
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date created;

    protected Business() {}

    /**
     * Constructor for the Business object
     * @param name  Name of the business
     * @param description   Brief description of the business
     * @param address   Business address
     * @param businessType  The type of business
     */
    public Business(String name, String description, String address, BusinessType businessType) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.businessType = businessType;
    }

    /**
     * This method is called when a post request is made through the controller to create a business
     * Created date is generated automatically
     * The logged in user is set as business administrator by default
     */
    public void createBusiness(User owner) {
        this.administrators.add(owner);
        this.created = new Date();
    }
}
