package com.galvanize.invoicify.services;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private Adapter _adapter;

	@Override
	public @NotNull	UserDetails loadUserByUsername(@NotNull final String username) throws UsernameNotFoundException {
		final Optional<User> user = this._adapter.getUserByUserName(username);

		return user.orElseThrow( () -> new UsernameNotFoundException(username) );

	}

}