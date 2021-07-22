package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.function.Supplier;

@MappedSuperclass()
public abstract class BillingRecordDataAccess<T extends BillingRecord> implements IDataAccess<T> {

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "in_use", nullable = false)
    @JsonProperty(value = "in_use")
    public boolean inUse;

    @Column(nullable = false)
    public String description;

    @Column(name = "billing_record_company_id", nullable = false)
    public long companyId;

    @Column(name = "billing_record_created_by", nullable = false)
    public long createdBy;

    @Transient
    public User user;

    @Transient
    public Company company;

    // constructor/s

    public BillingRecordDataAccess(){

    }

    // get & set


    public boolean isInUse() {
        return inUse;
    }

    public boolean getInUse(){
        return this.isInUse();
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public Company getCompany() {
        return company;
    }

    //    public Long getId() {
//        return id;
//    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    //    public void setId(Long id) {
//        this.id = id;
//    }


    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // method/s


    @Override
    public <M extends T> M convertToModel(Supplier<M> supplier) {

//        System.out.println(this.getCompany().getName());
//        System.out.println(this.getUser().getUsername());

        final M billingRecord = supplier.get();
        billingRecord.setClient(this.getCompany());
        billingRecord.setDescription(this.getDescription());
        billingRecord.setInUse(this.getInUse());
        billingRecord.setId(this.getId());
        billingRecord.setCreatedBy(this.getUser());

        return billingRecord;
    }

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends T> void convertToDataAccess(M modelObject) {
        this.setCreatedBy(modelObject.getCreatedBy().getId());
        this.setUser(modelObject.getCreatedBy());
        this.setCompany(modelObject.getClient());
        this.setCompanyId(modelObject.getClient().getId());
        this.setDescription(modelObject.getDescription());
        if(modelObject.getId() != null)
            this.setId(modelObject.getId());
    }
}
