package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h2>
 *     todo: add descrption here
 * </h2>
 * */

@Service
public final class Adapter {

    private final UserRepository _userRepository;
    private final PasswordEncoder _encoder;

    @Autowired
    public Adapter(UserRepository userRepository, PasswordEncoder encoder){
        this._userRepository = userRepository;
        this._encoder = encoder;
    }


    // ...stubs go below
    // add your method signatures to complete your user stories here

    /**
     * <p>
     *      Handles the request from the controller by utilziing the userRepository; looks up the
     *      company with teh given
     * </p>
     * @param username
     * @return Optional<User> : a user is returned
     * */

    public @NotNull Optional<User> getUserByUserName(final String username){

        final Optional<UserDataAccess> userDataAccessOptional = this._userRepository.findByUsername(username);

        return userDataAccessOptional.map(userDataAccess -> userDataAccess.convertToModel(User::new));

    }

    /**
     *<p>
     *     Handles the request from the controller by utilizing the userRepository; it locates the user to be updated
     *     by the given ID, then cross checks the given userName with existing userNames to prevent duplication of user ids.
     *     The userName is updated to the given userName, which then verifies the password is not null or blank.
     *     Then saves the user as a DAO in the user table and converts the DAO to a user Model.
     *</p>
     * @param user
     * @param id
     * @return User :
     * */


    public @NotNull User updateUser(@NotNull final User user, @NotNull final Long id) throws DuplicateUserException {
        UserDataAccess currentUserData = this._userRepository.findById(id).get();

        if (user.getUsername() != null && !user.getUsername().equals("")) {
            //check if there's another user with the given username and prevent duplication of user ids.
            if (isUserExists(user.getUsername())){
                throw new DuplicateUserException("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
            }
            currentUserData.setUsername(user.getUsername());
        }

        if (user.getPassword() != null && !user.getPassword().equals("")) {
            currentUserData.setPassword(_encoder.encode(user.getPassword()));
        }

        return _userRepository.save(currentUserData).convertToModel((User::new));
    }

    /**
     *<p>
     *     Handles the request from the controller by utilizing the userRepository; it cross checks existing userName
     *     with to verify that the given userName does not already exist in the user table.
     *     Then saves the user as a DAO in the user table and converts the DAO to a user Model.
     *</p>
     * @param user -> takes in the specific user provided
     * @return User : With the userRepository, it checks if the user exists, throws a DuplicateUserExecption,
     * otherwise, a new user DAO is instantiated then converted to a user model.
     * @throws DuplicateUserException : It addresses the issue of computing logic against duplication
     * of keys. Since the User table must contain non-null, unique String name entries,
     * this exception prevents the user from assigning a name to a User that already exists
     * in the table. This aslo handles redirecting the user in these instances and
     * prompting to adjust serialization so table integrity in tact and aligned with the rest of the system
     * */

    public @NotNull User createUser(@NotNull final User user) throws DuplicateUserException {
        if (isUserExists(user.getUsername())){
            throw new DuplicateUserException("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
        }
        UserDataAccess userDataAccess = new UserDataAccess();
        userDataAccess.setUsername(user.getUsername());
        userDataAccess.setPassword(_encoder.encode(user.getPassword()));
        return _userRepository.save(userDataAccess).convertToModel((User::new));
    }

    /**
     *<p>
     *      Handles the request from the controller by utilizing userRepository; it counts the user with the given
     *      userName to validate if the specified user exists. If it does, it returns true as a boolean.
     *</p>
     * @param userName -> specific user to be verified to exist in data store
     * @return boolean -> validates if specified user exists, then counts the number of times the userName is listed to
     * verify that the user exists. If it does exist, it returns true.
     * @exception DuplicateUserException -> It addresses the issue of computing logic against duplication of keys.
     * Since the User table must contain non-null, unique String name entries, this exception prevents the user from
     * assigning a name to a User that already exists in the table. This also handles redirecting the user in these
     * instances and prompting to adjust serialization so table integrity in tact and aligned with the rest of the system
     * */

    private @NotNull boolean isUserExists(@NotNull final String userName) throws DuplicateUserException {
        return this._userRepository.findByUsername(userName).isPresent();
    }


    /**
     * <p>
     *     Conveys the logic requested by the controller. It reads all users from
     *     the table, collects the DAO, and converts each of them into a User model
     *     and persists them into a list.
     * </p>
     * @return List<User> : finds all the users, streams and maps them together,
     * and converts each into a User model. Then returns it as a list.
     */

    public List<User> findAll() {
        return _userRepository.findAll().stream().map(userDataAccess -> userDataAccess.convertTo(User::new)).collect(Collectors.toList());
    }

    /**
     * <p>
     *     Handles the request from the controller by looking up the user with the
     *     provided id, then converts the DAO to a User Model.
     * </p>
     * @param id -> requires and id param for the requested user
     * @return : User -> finds the specific user DAO by ID and converts it
     * to a user Model.
     */

    public @NotNull User findUser(@NotNull final Long id) {
        return _userRepository.findById(id).map(userDataAccess -> userDataAccess.convertToModel(User::new)).get();
    }
}
