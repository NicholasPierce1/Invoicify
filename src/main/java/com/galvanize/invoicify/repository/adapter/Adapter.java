package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class Adapter {

    private final UserRepository _userRepository;

    @Autowired
    public Adapter(UserRepository userRepository){
        this._userRepository = userRepository;
    }

    // ...stubs go below
    // add your method signatures to complete your user stories here

    public @NotNull Optional<User> getUserByUserName(final String username){

        final Optional<UserDataAccess> userDataAccessOptional = this._userRepository.findByUsername(username);

        return userDataAccessOptional.map(userDataAccess -> userDataAccess.convertTo(User::new));

    }

}
