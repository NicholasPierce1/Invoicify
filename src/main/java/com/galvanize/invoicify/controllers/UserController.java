package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.galvanize.invoicify.models.User;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder encoder;

	@PutMapping("{id}")
	public User updateUser(Authentication auth, @RequestBody User user, @PathVariable Long id) throws Exception {
		User currentUserData = this.userRepository.findById(id).orElseThrow(Exception::new);
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

	@GetMapping
	public List<User> getUsers() {
		return this.userRepository.findAll();
	}

	@GetMapping("{id}")
	public User getUser(@PathVariable Long id){
		return this.userRepository.findById(id).get();
	}

}