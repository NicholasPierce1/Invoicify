package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private Adapter adapter;

	public UserController(Adapter adapter) {
		this.adapter = adapter;
	}


	@PutMapping("{id}")
	public User updateUser(Authentication auth, @RequestBody User user, @PathVariable Long id) throws Exception {
		 return adapter.updateUser(user, id);
	}

	@PostMapping
	public Optional<User> createUser(@RequestBody User user) {
		try{
			return Optional.of(adapter.createUser(user));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return Optional.empty();
		}
	}

	@GetMapping
	public List<User> getUsers() {
		return adapter.findAll();
	}

	@GetMapping("{id}")
	public User getUser(@PathVariable Long id){
		return adapter.findUser(id);
	}

}