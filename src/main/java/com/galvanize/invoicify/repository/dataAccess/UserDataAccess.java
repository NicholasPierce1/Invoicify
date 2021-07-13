package com.galvanize.invoicify.repository.dataAccess;


import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataAccess.definition.IDataAccess;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.function.Supplier;

@Entity
public final class UserDataAccess implements IDataAccess<User> {

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // constructor/s


    // get & set


    // method/s


    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public User convertTo(Supplier<User> supplier) {
        return null;
    }

}
