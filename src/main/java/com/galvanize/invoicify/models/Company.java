package com.galvanize.invoicify.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>
 *       The Model Object that is unwrapped from its DataAccessObject; contains two fields that describe the company.
 * </h2>
 * @field id -> Long id number that is assigned to the Model Object
 * @field name -> String name that is assigned to the Model Object
 */

public final class Company {

    private Long id;

    private String name;

    public Company() {
    }

    public Company(Long id) {
        this.id = id;
    }

    /**
     * <p>
     *     This constructor is used to convey properties that signify Company Model. It can be overloaded to have a one
     *     arg constructor or no arg constructor.
     * </p>
     * @param id -> Describes the auto-serialized number assigned by Spring.
     * @param name -> The string name representation for the company.
     */
    public Company(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * <p>
     *     This gets the company id.
     * </p>
     * @return id -> retrieves the id that was assigned to that company.
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>
     *     This id is associated to the DataAccessObject when converted to the Model.
     * </p>
     * @param id -> takes in the id that was assigned to the Company DataAccessObject and unwrapped into the Model.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>
     *     This gets the company name.
     * </p>
     * @return name -> retrieves the name that was assigned to that company.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     *     The String name that is parsed from the User DataAccessObject input.
     * </p>
     * @param name -> sets the name of company assigned by the DataAccessObject after conversion.
     */
    public void setName(String name) {
        this.name = name;
    }
}
