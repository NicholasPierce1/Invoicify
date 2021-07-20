package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.function.Supplier;

@Entity
@Table(name = "company")
public class CompanyDataAccess implements IDataAccess<Company> {

    //fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String name;

    // constructor

    public CompanyDataAccess(){}

    public CompanyDataAccess(Long id, String name) {
        this.id=id;
        this.name=name;
    }


    //set & get

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    // methods

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends Company> M convertToModel(Supplier<M> supplier) {

        final M company = supplier.get();

        company.setId(this.getId());
        company.setName(this.getName());

        return company;

    }

    @Override
    public <M extends Company> void convertToDataAccess(M modelObject) {
        this.setId(modelObject.getId());
        this.setName(modelObject.getName());
    }

    @Override
    public boolean equals(Object a){

        if(a == this)
            return true;

        if(a == null)
            return false;

        if(!(a instanceof CompanyDataAccess))
            return false;

        final CompanyDataAccess companyDataAccess = (CompanyDataAccess)a;

        final boolean baseCondition =
                companyDataAccess.getName().equals(this.getName());

        return companyDataAccess.getId() == null ?
                baseCondition
                :
                baseCondition && companyDataAccess.getId().equals(this.getId());

    }

}
