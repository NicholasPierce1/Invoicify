package com.galvanize.invoicify.configuration;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.repositories.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {

	public SeedData(UserRepository userRepository, PasswordEncoder encoder) {
		System.out.println("called");
		User admin = userRepository.save(new User("admin", encoder.encode("admin")));
		User userBob = userRepository.save(new User("bob", encoder.encode("password")));
		User userBobby = userRepository.save(new User("bobby", encoder.encode("password")));
		User userSally = userRepository.save(new User("sally", encoder.encode("password")));
		User userCindy = userRepository.save(new User("cindy", encoder.encode("password")));
	}

}