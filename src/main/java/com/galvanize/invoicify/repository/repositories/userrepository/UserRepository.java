package com.galvanize.invoicify.repository.repositories.userrepository;

import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * <h2>
 *     This interface extends the JPARepository, which takes in a UserDataAccess Object, along
 *      with its serialized Long id. This repository defines a method that finds a user by
 *      its user name. Important to note that it inherits the methods from the JPARepository,
 *      so this interface is not confined to just the method that is present. Most of
 *      the methods called on by the UserRepository actually are inherited from
 *      parent interface
 * </h2>
 */
@Repository
public interface UserRepository extends JpaRepository<UserDataAccess, Long> {

	//add special method to find users by username
	Optional<UserDataAccess> findByUsername(String username);

}
