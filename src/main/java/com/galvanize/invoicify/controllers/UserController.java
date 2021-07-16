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

		return userRepository.save(currentUserData);
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