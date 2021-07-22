package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.Date;
import java.util.function.Supplier;

@Entity
@Table(name = "invoice")
public class InvoiceDataAccess implements IDataAccess<Invoice> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private long id;
    @Column(name = "invoice_company_id", nullable = false)
    private long companyId;
    @Column(name = "created_on",nullable = false)
    private Date createdOn;
    @Column(name = "created_by",nullable = false)
    private long createdBy;
    @Column(name = "description", nullable = false)
    private String description;

    public InvoiceDataAccess() {

    }
    public InvoiceDataAccess(long companyId, Date createdOn, long createdBy, String description) {
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
        return null;
    }

    @Override
    public <M extends Invoice> void convertToDataAccess(M modelObject) {

    }



}
