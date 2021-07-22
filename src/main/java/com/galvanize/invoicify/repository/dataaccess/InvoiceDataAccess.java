package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.InvoiceLineItem;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Supplier;

@Entity
@Table(name = "invoice")
public class InvoiceDataAccess implements IDataAccess<Invoice> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")

    @JsonProperty(value = "invoice_id")
    private long id;

    @Column(name = "invoice_company_id", nullable = false)
    @JsonProperty(value = "invoice_company_id")
    private long companyId;

    @Column(name = "created_on",nullable = false)
    @JsonProperty(value = "created_on")
    private LocalDate createdOn;

    @Column(name = "created_by",nullable = false)
    @JsonProperty(value = "created_by")
    private long createdBy;

    @Column(name = "description", nullable = false)
    private String description;

    @Transient
    private Company company;
    @Transient
    private User user;
    @Transient
    private ArrayList<InvoiceLineItem> lineItems = new ArrayList<InvoiceLineItem>();

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setLineItems(ArrayList<InvoiceLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<InvoiceLineItem> getLineItems() {
        return lineItems;
    }

    public Company getCompany() {
        return company;
    }

    public User getUser() {
        return user;
    }

    public InvoiceDataAccess() {

    }
    public InvoiceDataAccess(long companyId, LocalDate createdOn, long createdBy, String description) {
        this.companyId = companyId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends Invoice> M convertToModel(Supplier<M> supplier) {
        M invoice = supplier.get();
        invoice.setId(this.getId());
        invoice.setCompany(this.getCompany());
        invoice.setCreatedBy(this.getUser());
        invoice.setInvoiceDescription(this.getDescription());
        invoice.setLineItems(this.getLineItems());
        return invoice;
    }

    @Override
    public <M extends Invoice> void convertToDataAccess(M modelObject) {

    }


}
