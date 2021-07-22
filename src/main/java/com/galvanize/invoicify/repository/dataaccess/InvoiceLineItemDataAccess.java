package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.Date;
import java.util.function.Supplier;

@Entity
@Table(name = "invoice_line_item")
public class InvoiceLineItemDataAccess implements IDataAccess<InvoiceLineItemDataAccess> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_line_item_id")
    @JsonProperty("invoice_line_item_id")
    private long id;

    @Column(name = "billing_record_id", nullable = false)
    @JsonProperty("billing_record_id")
    private long billingRecordId;

    @Column(name = "created_on", nullable = false)
    @JsonProperty("created_on")
    private Date createdOn;

    @Column(name = "created_by", nullable = false)
    @JsonProperty("created_by")
    private long createdBy;

    @Column(name = "invoice_id", nullable = false)
    @JsonProperty("invoice_id")
    private long invoiceId;

    public InvoiceLineItemDataAccess(long billingRecordId, Date createdOn, long createdBy, long invoiceId) {
        this.id = id;
        this.billingRecordId = billingRecordId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.invoiceId = invoiceId;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBillingRecordId() {
        return billingRecordId;
    }

    public void setBillingRecordId(long billingRecordId) {
        this.billingRecordId = billingRecordId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends InvoiceLineItemDataAccess> M convertToModel(Supplier<M> supplier) {
        return null;
    }

    @Override
    public <M extends InvoiceLineItemDataAccess> void convertToDataAccess(M modelObject) {

    }


}
