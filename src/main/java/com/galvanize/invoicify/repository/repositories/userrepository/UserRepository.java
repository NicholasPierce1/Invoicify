package com.galvanize.invoicify.repository.repositories.userrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.galvanize.invoicify.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//add special method to find users by username
	User findByUsername(String username);

	/*@Query("SELECT count(*) from app_user WHERE u.userName = ?1" )
	int countUsersByUserName(String username);*/

}
