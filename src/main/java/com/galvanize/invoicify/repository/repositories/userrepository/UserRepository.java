package com.galvanize.invoicify.repository.repositories.userrepository;

import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.galvanize.invoicify.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDataAccess, Long> {

	//add special method to find users by username
	Optional<UserDataAccess> findByUsername(String username);

	@Query("SELECT count(*) from app_user WHERE u.userName = ?1" )
	int countUsersByUserName(String username);

}
