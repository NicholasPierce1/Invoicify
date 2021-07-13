package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class Adapter {

    private final UserRepository _userRepository;

    @Autowired
    public Adapter(UserRepository userRepository){
        this._userRepository = userRepository;
    }

    // ...stubs go below
    // add your method signatures to complete your user stories here

}
