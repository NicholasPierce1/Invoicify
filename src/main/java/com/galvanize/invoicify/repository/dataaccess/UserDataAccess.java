package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import javax.persistence.*;
import java.util.function.Supplier;

/**
 * <h2>
 *     UserDataAccess
 * </h2>
 * <p>
 *     Spring bean Entity that manages the connection between the database and the Model.
 *     The UserDataAccess corresponds to the User table in the database. It contains fields which
 *     directly express the columns directly in the table. UserDataAccess implements the IDataAccess Interface
 *     and inherits the methods: createDataAccess, convertToModel, convertToDataAccess; all of which wraps and unwraps
 *     the User model while restricting transaction to the database.
 * </p>
 */

@Entity(name = "app_user")
public final class UserDataAccess implements IDataAccess<User> {

    // fields
    /**
     * <p>
     *     Autogenerated serialization id when persisted to that database.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @JsonProperty("user_id")
    public Long id;

    /**
     * <p>
     *     String password that is stored in non-nullable user_name column of the user table.
     * </p>
     */
    @Column(nullable = false)
    private String password;

    /**
     * <p>
     *     Represents the username of an User.
     *     Stored within remote date store for query reference and selection.
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String username;

    // constructor/s

    public UserDataAccess(){}

    /**
     * <p>
     *     This constructor receives user input (String username, String password)
     *     and use it to initialize a newly created UserDataAccessObject before it is used.
     *     It cannot have a one arg constructor because the user_name column in the table cannot have null entries,
     *     therefore, there can only be a no args or all arg constructor.
     * </p>
     * @param username -> String username of the user that is assigned by the user when created and saved
     * @param password -> String password of the user that is assigned by the user when created and saved
     * */

    public UserDataAccess(String username, String password) {
        this.username = username;
        this.password = password;
    }



    // get & set

    /**
     *<p>
     *  This gets the user id in the user table.
     *</p>
     * @return id -> id stored in the user_id field in User table.
     * */

    public Long getId() {
        return id;
    }

    /**
     *<p>
     *  This gets the password that is stored in the user table.
     *</p>
     * @return password -> retrieves the password that was assigned to that user.
     * */

    public String getPassword() {
        return password;
    }

    /**
     * <p>
     *     This gets the user name that is stored in the User table.
     * </p>
     * @return username -> retrieves the username that was assigned to that user.
     * */

    public String getUsername() {
        return username;
    }

    /**
     * <p>
     *     This sets the serialized id that was assigned to the User DataAccessObject.
     * </p>
     * @param id -> takes in the serialized id that was assigned to the user DataAccessObject
     * */

    public void setId(Long id) {
        this.id = id;
    }

    /***
     * <p>
     *     The sets the string password for the User that is stored in the user table.
     * </p>
     * @param password -> set the password of the User assigned to that User
     */

    public void setPassword(String password) {
        this.password = password;
    }

    /***
     * <p>
     *     This sets the username that is stored for the user in the user table.
     * </p>
     * @param username -> sets the username of the User
     */

    public void setUsername(String username) {
        this.username = username;
    }

    // method/s

    /**
     * <p>
     *     Converts a Model Object to a DataAccessObject and sets the name and id.
     * </p>
     * @param <M>: A Model Object used to convert DataAccessObject
     */
    @Override
    public <M extends User> M convertToModel(Supplier<M> supplier) {
        M user = supplier.get();
        user.setId(this.getId());
        user.setUsername(this.getUsername());
        user.setPassword(this.getPassword());
        return user;
    }

    /**
     * <p>
     *     Converts a Model Object to a DataAccessObject and sets the name and id.
     * </p>
     * @param <M>: A Model Object used to convert DataAccessObject
     */
    @Override
    public <M extends User> void convertToDataAccess(M modelObject) {
        this.setId(modelObject.getId());
        this.setUsername(modelObject.getUsername());
    }

}
