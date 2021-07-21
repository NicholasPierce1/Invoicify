package com.galvanize.invoicify.models;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Date;


public class Invoice {
    private long id;
    private Company company;
    private Date createdOn;
    private User createdBy;
    private String invoiceDescription;
    private ArrayList<BillingRecord> lineItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    public ArrayList<BillingRecord> getLineItems() {
        return lineItems;
    }

    public void setLineItems(ArrayList<BillingRecord> lineItems) {
        this.lineItems = lineItems;
    }


}
