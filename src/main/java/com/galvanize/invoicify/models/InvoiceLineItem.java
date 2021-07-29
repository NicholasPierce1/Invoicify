package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

/**
 * <h2>
 *     InvoiceLineItem
 * </h2>
 * <p>
 *     this invoiceLineItem object is only used for storing invoice billing record line items that are returned from the database through InvoiceDataAccess.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceLineItem {

    /**
     * <p>
     *     invoice line item id.
     * </p>
     */
    private long id;

    /**
     * <p>
     *     invoice line date created on - this is normally the same as when the invoice is created.
     * </p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreatedOn;

    /**
     * <p>
     *     invoice line item created by - normally this is the same as who created the invoice.
     * </p>
     */
    private User createdBy;

    /**
     * <p>
     *     the invoice this invoice line item is created for
     * </p>
     */
    private Invoice invoice;

    /**
     * <p>
     *     for each invoice line item, a billing record must exist, this is the field to store the billing record details for each invoice line item.
     * </p>
     */
    private BillingRecord billingRecord;

    /**
     * <p>
     *     return id.
     * </p>
     * @return id.
     */
    public long getId() {
        return id;
    }

    /**
     * <p>
     *     sets the id.
     * </p>
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>
     *     returns billingRecord
     * </p>
     * @return billingRecord
     */
    public BillingRecord getBillingRecord() {
        return billingRecord;
    }

    /**
     * <p>
     *     set billingRecord
     * </p>
     * @param billingRecord - BillingRecord
     */
    public void setBillingRecord(BillingRecord billingRecord) {
        this.billingRecord = billingRecord;
    }

    /**
     * <p>
     *     get date created on.
     * </p>
     * @return DateCreatedOn
     */
    public LocalDate getDateCreatedOn() {
        return dateCreatedOn;
    }

    /**
     * <p>
     *    set dateCreatedOn
     * </p>
     * @param dateCreatedOn
     */
    public void setCreatedOn(LocalDate dateCreatedOn) {
        this.dateCreatedOn = dateCreatedOn;
    }

    /**
     * <p>
     *     return User createdBy
     * </p>
     *
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * <p>
     *    set createdBy
     * </p>
     * @param createdBy
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * <p>
     *     get invoice
     * </p>
     * @return invoice
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * <p>
     *     set invoice
     * </p>
     * @param invoice
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }


}
