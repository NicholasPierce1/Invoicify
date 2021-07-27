package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h2>
 *     This invoice object is used for mapping the invoice request on create endpoint and this object is populated within InvoiceDataAccess and returned to the client.
 * </h2>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Invoice {

    //fields
    /**
     * <p>
     *     invoice id
     * </p>
     */
    private long id;

    /**
     * <p>
     *     Company the invoice is tied to
     * </p>
     */
    private Company company;

    /**
     * <p>
     * Invoice createdOn that has yyyy-MM-dd pattern
     * </p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdOn;

    /**
     *<p>
     *    this is the invoice's creator.
     *</p>
     */
    private User createdBy;

    /**
     * <p>
     *     the invoice's description that is passed in upon creation of the invoice.
     * </p>
     */
    private String invoiceDescription;

    /**
     * <p>
     *     the list of invoice items that contains the billing record that go to the invoice.
     * </p>
     */
    private List<InvoiceLineItem> lineItems;

    /**
     * <p>
     *     this is only passed in from the client request on invoice creation
     * </p>
     */
    private List<Long> recordIds = new ArrayList<Long>();


    //getters and setters

    /**
     * <p>returns the invoice id</p>
     * @return Long id
     */
    public long getId() {
        return id;
    }

    /**
     * <p>set the instance's invoice id</p>
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>returns the company object</p>
     * @return Company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * <p>set the instance's company</p>
     * @Param company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * <p>method to return the createdOn field</p>
     * @return createdOn
     */
    public LocalDate getCreatedOn() {
        return createdOn;
    }

    /**
     * <p>set createdOn field.</p>
     * @param createdOn
     */
    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * <p>this is a method to return createdBy.</p>
     *
     * @return createdBy User
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * <p>Set createdBy Object</p>
     *
     * @param createdBy - User object
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * <p>this is the method to return the invoice's description</p>
     *
     * @return invoiceDescription
     */
    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    /**
     * <p>set invoiceDescription</p>
     * @param invoiceDescription - String
     */
    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    /**
     * <p>return the list of invoice line items</p>
     * @return
     */
    public List<InvoiceLineItem> getLineItems() {
        return lineItems;
    }

    /**
     * <p>
     *     set the list of invoice line items
     * </p>
     * @param lineItems - List
     */
    public void setLineItems(List<InvoiceLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    /**
     * <p>
     *     return a list of record Ids
     * </p>
     * @return
     */
    public List<Long> getRecordIds() {
        return recordIds;
    }

    /**
     * <p>
     *     return list of recordIds
     * </p>
     * @param recordIds
     */
    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }
}
