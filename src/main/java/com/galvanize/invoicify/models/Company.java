package com.galvanize.invoicify.models;

/**
 * <h2>
 *     Company
 * </h2>
 * <p>
 *       The Model Object that is unwrapped from its DataAccessObject; contains two fields that describe the company.
 * </p>
 */

public final class Company {

    /**
     * <p>
     *     Long id number that is assigned to the Model Object
     * </p>
     */
    private Long id;

    /**
     * <p>
     *     String name that is assigned to the Model Object
     * </p>
     */
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
     *     This is the reflection of the company id that has been converted.
     * </p>
     * @return id -> retrieves the id that was assigned to that company DataAccessObject.
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
     *     retrieves the  name of a converted Company DataAccessObject.
     * </p>
     * @return name -> retrieves the name of a converted Company DataAccessObject that was assigned to that company.
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
