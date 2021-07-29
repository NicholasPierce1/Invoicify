package com.galvanize.invoicify.services;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 *<p>
 *     The AppUserDetailService class implements the loadUserByUsername function.
 *     The loadUserByUsername function locates the user based on the username. The result of the search,
 *     if existing, then validates the credentials given through the login form with the user
 *     information retrieved by the adapter. If non-existing, a UsernameNotFoundException is thrown.
 *</p>
 * */

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private Adapter _adapter;

	/**
	 * <p>
	 *      The loadUserByUsername method locates the user based on the username.
	 *      The result of the search, if existing, then validates the credentials given
	 *      through the login form with the user information retrieved through the UserDetailsService.
	 *      This is retrieve by the adapter then returned as a user model.
	 * </p>
	 * @param username the String username provided for the given user
	 * @return UserDetails the details of a given user
	 * */

	@Override
	public @NotNull	UserDetails loadUserByUsername(@NotNull final String username) throws UsernameNotFoundException {
		final Optional<User> user = this._adapter.getUserByUserName(username);

		return user.orElseThrow( () -> new UsernameNotFoundException(username) );

	}

}