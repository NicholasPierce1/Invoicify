package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.Entity;
import java.util.function.Supplier;

@Entity
public class CompanyDataAccess implements IDataAccess<Company> {

    //fields

    //set & get

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public Company convertTo(Supplier<Company> supplier) {
        return null;
    }


}
