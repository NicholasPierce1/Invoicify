package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.function.Supplier;

@MappedSuperclass()
abstract class BillingRecordDataAccess<T extends BillingRecord> implements IDataAccess<T> {

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "in_use", nullable = false)
    public boolean inUse;

    @Column(nullable = false)
    public String description;

    @Column(name = "company_id", nullable = false)
    public long companyId;


    // constructor/s

    public BillingRecordDataAccess(){

    }

    // get & set


    public String getDescription() {
        return description;
    }

//    public Long getId() {
//        return id;
//    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    // method/s


    @Override
    public <M extends T> M convertTo(Supplier<M> supplier) {
        return null;
    }

    @Override
    public void createDataAccess(Object[] dbo) {

    }
}
