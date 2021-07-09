package com.galvanize.invoicify.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.galvanize.invoicify.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//add special method to find users by username
	User findByUsername(String username);

}
