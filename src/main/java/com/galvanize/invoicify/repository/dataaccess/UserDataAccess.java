package com.galvanize.invoicify.repository.dataaccess;


import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.function.Supplier;

@Entity(name = "app_user")
public final class UserDataAccess implements IDataAccess<User> {

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

    @Override
    public User convertTo(Supplier<User> supplier) {
        return null;
    }

}
