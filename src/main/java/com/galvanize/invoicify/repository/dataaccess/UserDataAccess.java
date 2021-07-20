package com.galvanize.invoicify.repository.dataaccess;


import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

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
    public <M extends User> M convertToModel(Supplier<M> supplier) {
        return null;
    }

    @Override
    public void convertToDataAccess(User modelObject) {

    }

}
