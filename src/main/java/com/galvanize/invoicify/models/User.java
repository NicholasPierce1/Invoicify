package com.galvanize.invoicify.models;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <h2>
 *		The Model Object that is unwrapped from its DataAccessObject: contains four fields
 *		that describe a User and where it lies on the data base. The
 * </h2>
 * @field serialVersionUID -> Long number attribute that is an identifier that is used to serialize an object of a User.
 * @field id -> Long id number that is assigned to the Model Object
 * @field password -> String username that is assigned to the Model Object
 * @field username -> String password that is assigned to the Model Object.
 * @implements UserDetails -> interface that checks for authentication.
 * */

public final class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String password;

	private String username;

	public User() {
	}

	/**
	 * <p>
	 *     This constructor is used to convey properties that are assigned to the User Model.
	 *     It can be overloaded to have a one arg constructor as well as a no arg constructor.
	 * </p>
	 * @param username -> The string representation for the password.
	 * @param password -> The string representation for the password.
	 * */

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}


	/**
	 * <p>
	 *     This methods gets the password stored for the User DataAccessObject
	 *     stored in the user table.
	 * </p>
	 * @return password -> password stored in the user_password field in the User table.
	 */

	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * <p>
	 *     This method gets the String username for the User DataAccessObject
	 *     stored in the user table
	 * </p>
	 * @return username -> string username stored in the user_password field in the user table.
	 * */

	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * <p>
	 *		This method checks if the account is non-expired in the user data store.
	 * </p>
	 * @return true -> return true if the account credentials are non-expired.
	 * */

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * <p>
	 *		This method checks if the authentication for the user is locked.
	 * </p>
	 * @return true -> boolean returns true of the account is locked.
	 * */

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * <p>
	 *		Checks if the credentials entered are expired.
	 * </p>
	 * @return true -> boolean returns true if the credentials stored are expired.
	 * */

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * <p>
	 *     This is the reflection of the user id in the database that has been
	 *     converted.
	 * </p>
	 * @return id -> retrieves the id that was assigned to that user DataAccessObject.
	 * */

	public Long getId() {
		return id;
	}

	/**
	 * <p>
	 *     This id is assigned to the DataAccessObject when converted to the User Model.
	 * </p>
	 * @param id -> takes in the id that was assigned to the DataAccessObject and unwrapped into the
	 *           User Model.
	 * */

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * <p>
	 *		This sets String password
	 * </p>
	 * @param password -> sets the password of the User assigned to the DataAccessObject after conversion.
	 * */

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * <p>
	 *		The String username that is parsed from the User DataAccessObject
	 * </p>
	 * @param username -> sets the username of the User assigned to the DataAccessObject.
	 * */

	public void setUsername(String username) {
		this.username = username;
	}


}