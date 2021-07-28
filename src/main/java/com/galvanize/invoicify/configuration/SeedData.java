package com.galvanize.invoicify.configuration;

import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {

	public SeedData(UserRepository userRepository, PasswordEncoder encoder) {
		UserDataAccess admin = userRepository.save(new UserDataAccess("admin", encoder.encode("admin")));
		UserDataAccess userBob = userRepository.save(new UserDataAccess("bob", encoder.encode("password")));
		UserDataAccess userBobby = userRepository.save(new UserDataAccess("bobby", encoder.encode("password")));
		UserDataAccess userSally = userRepository.save(new UserDataAccess("sally", encoder.encode("password")));
		UserDataAccess userCindy = userRepository.save(new UserDataAccess("cindy", encoder.encode("password")));
	}

}