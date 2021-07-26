package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.configuration.InvoicifyConfiguration;
import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.InvoiceLineItem;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Supplier;

@Entity
@Table(name = "invoice_line_item")
public class InvoiceLineItemDataAccess implements IDataAccess<InvoiceLineItem> {

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

    // transient members
    @Transient
    private UserDataAccess user;

    @Transient
    private BillingRecordDataAccess<? extends BillingRecord> billingRecord;

    @Transient
    DateTimeFormatter dateTimeFormatter = InvoicifyConfiguration.getStaticDateFormatter();

    public InvoiceLineItemDataAccess(long billingRecordId, Date createdOn, long createdBy, long invoiceId) {
        this.id = id;
        this.billingRecordId = billingRecordId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.invoiceId = invoiceId;
    }

    public InvoiceLineItemDataAccess(){}


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

    public BillingRecordDataAccess<? extends BillingRecord> getBillingRecord() {
        return billingRecord;
    }

    public UserDataAccess getUser() {
        return user;
    }

    public void setUser(UserDataAccess user) {
        this.user = user;
    }

    public void setBillingRecord(BillingRecordDataAccess<? extends BillingRecord> billingRecord) {
        this.billingRecord = billingRecord;
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
    public <M extends InvoiceLineItem> M convertToModel(Supplier<M> supplier) {
        M invoiceLineItem = supplier.get();
        invoiceLineItem.setId(this.getId());
        invoiceLineItem.setCreatedOn(this.getCreatedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        invoiceLineItem.setCreatedBy(this.getUser().convertToModel(User::new));
        return invoiceLineItem;
    }

    //todo: implement and use in create invoice data access story (or whereas applicable)
    @Override
    public <M extends InvoiceLineItem> void convertToDataAccess(M modelObject) {

    }


}
