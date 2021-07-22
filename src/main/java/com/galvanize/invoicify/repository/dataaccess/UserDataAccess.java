package com.galvanize.invoicify.repository.dataaccess;


import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.function.Supplier;


/**
 * <h1>UserDataAccess</h1>
 * <h2>Type: Class</h2>
 *
 * Implementing IDataAccess methods to create User data access objects(DAOs)
 * from the data from the database
 */

@Entity(name = "app_user")
public final class UserDataAccess implements IDataAccess<User> {

    /**
     *<p>
     * This is a Class that takes the data from the database and
     * the constructor of the specified entity to create User data access objects
     *</p>
     *
     * @param data: Object[] data representing the information from database.
     * @param dataList: generic list of data access objects
     * @param typeConstructor: Lambda typeConstructor representing the constructor for the specified entity type.
     */

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    // constructor/s

    public UserDataAccess(){}

    public UserDataAccess(String username, String password) {
        this.username = username;
        this.password = password;
    }



    // get & set

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // method/s


    @Override
    public void createDataAccess(Object[] dbo) {

    }

    /**
     * <p>
     *     Converts a data access object to a model object
     * </p>
     * @param supplier: provides implementation of creating a model object.
     *                    NOTE: the default state set in the supplier may/will be written over.
     * @param <M>: A model type used to create data access reflections
     * @return a User model object of the reflect data access definition
     */

    @Override
    public <M extends User> M convertToModel(Supplier<M> supplier) {
        final M user = supplier.get();

        user.setId(this.getId());
        user.setUsername(this.getUsername());

        return user;
    }

    @Override
    public <M extends User> void convertToDataAccess(M modelObject) {
        this.setId(modelObject.getId());
        this.setUsername(modelObject.getUsername());
    }

}
