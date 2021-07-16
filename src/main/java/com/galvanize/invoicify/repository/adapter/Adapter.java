package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class Adapter {

    private final UserRepository _userRepository;


    @Autowired
    private PasswordEncoder encoder;

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


    public User updateUser(User user, Long id) throws Exception {
        UserDataAccess currentUserData = this._userRepository.findById(id).orElseThrow(Exception::new);

        if (user.getUsername() != null || !user.getUsername().equals("")) {
            //check if there's another user with the given username and prevent duplication of user ids.
			/*int userCountByUsername = this.userRepository.countUsersByUserName(user.getUsername());
			if (userCountByUsername > 1) {
				throw new Exception("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
			}*/
            currentUserData.setUsername(user.getUsername());
        }

        if (user.getPassword() != null || !user.getPassword().equals("")) {
            currentUserData.setPassword(encoder.encode(user.getPassword()));
        }
        return _userRepository.save(currentUserData).convertTo((User::new));
    }
}
