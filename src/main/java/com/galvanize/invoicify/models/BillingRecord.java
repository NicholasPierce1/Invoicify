package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BillingRecord {

    // fields
    /**
     * <p>
     *     Billing Record Id.
     * </p>
     */
    public Long id;

    /**
     * <p>
     *     represents the ID of a child User that created this Billing Record.
     * </p>
     */
    public User createdBy;

    /**
     * <p>
     *     indicates if the BillingRecord is currently in use to an Invoice.
     * </p>
     */
    public boolean inUse;

    /**
     * <p>
     *     delineates current state of the BillingRecord
     * </p>
     */
    public String description;

    /**
     * <p>
     *     represents the ID of a child Company that this BillingRecord is tethered to.
     * </p>
     */
    public Company client;

    // constructors

    public BillingRecord(){}

    // gets & sets

    /**
     * <p>
     *    returns id
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>
     *     returns description
     * </p>
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>
     *     returns client
     * </p>
     * @return a Company client object
     */
    public Company getClient() {
        return client;
    }

    /**
     * returns User Object createdBy
     *
     * @return user object createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * set id
     * @param id add doc here
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * set inUse
     *
     * @param inUse add doc here
     */
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     * set description
     * @param description add doc here
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * set client
     * @param client add doc here
     */
    public void setClient(Company client) {
        this.client = client;
    }

    /**
     * set createdBy
     * @param createdBy add doc here
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    // methods

    // abstract method for a field accessor

    /**
     * return the total
     * @return double representing the total cost of the billing record
     */
    public abstract double getTotal();
}
