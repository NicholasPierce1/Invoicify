package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galvanize.invoicify.models.User;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder encoder;

	@PutMapping("{id}")
	public User updateUser(Authentication auth, @RequestBody User user, @PathVariable Long id) {
		User currentUserData = this.userRepository.findOne(id);
		user.setId(currentUserData.getId());

		if (user.getPassword() == null) {
			user.setPassword(currentUserData.getPassword());
		} else {
			String encryptedPassword = encoder.encode(user.getPassword());
			user.setPassword(encryptedPassword);
		}

		return userRepository.save(user);
	}

	@PostMapping
	public User createUser(@RequestBody User user) {
		String password = user.getPassword();
		String encryptedPassword = encoder.encode(password);
		user.setPassword(encryptedPassword);
		userRepository.save(user);
		return user;
	}

}