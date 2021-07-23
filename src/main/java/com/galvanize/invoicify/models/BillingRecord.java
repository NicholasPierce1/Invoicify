package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RateBasedBillingRecord.class, name = "RateBasedBillingRecord"),
        @JsonSubTypes.Type(value = FlatFeeBillingRecord.class, name = "FlatFeeBillingRecord")
})
public abstract class BillingRecord {


    // fields
    public Long id;

    public User createdBy;

    public boolean inUse;

    public String description;

    public Company client;

    // constructors

    public BillingRecord(){}

    // gets & sets


    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Company getClient() {
        return client;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClient(Company client) {
        this.client = client;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    // methods

    // abstract method for a field accessor
    public abstract double getTotal();
}
