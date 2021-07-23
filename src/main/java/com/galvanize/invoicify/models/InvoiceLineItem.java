package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Transient;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceLineItem {
    private long id;
    private Date dateCreatedOn;

    @Transient
    private User createdBy;
    @Transient
    private Invoice invoice;
    @Transient
    private BillingRecord billingRecord;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BillingRecord getBillingRecord() {
        return billingRecord;
    }

    public void setBillingRecord(BillingRecord billingRecord) {
        this.billingRecord = billingRecord;
    }

    public Date getDateCreatedOn() {
        return dateCreatedOn;
    }

    public void setDateCreatedOn(Date dateCreatedOn) {
        this.dateCreatedOn = dateCreatedOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }


}
