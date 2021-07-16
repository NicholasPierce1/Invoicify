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
    public Company convertTo(Supplier<Company> supplier) {

        final Company company = supplier.get();

        company.setName(this.getName());
        company.setId(this.getId());

        return company;
    }


}
