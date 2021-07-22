package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;

import java.util.List;
import java.util.Optional;

/**
 * <h2>
 *     The UserController class is responsible for facilitating
 * 	   business logic to the adapter to create a response entity. It receives requests and based upon the endpoint
 * 	   it will return to the client a list of users, individual users, or it will perform
 * 	   a create, delete, or update CRUD action (implemented from JPA Repository)
 * </h2>
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

	/**
	 * <p>
	 *	The adapter is a connector between the IConvertible and IDataAcess
	 *	interfaces that connects the modelObject to UserDataAccess.
	 * </p>
	 * */

	private Adapter adapter;

	/**
	 * <p>
	 *	This constructor takes in the adapter and injects
	 *  into the UserController class.
	 * </p>
	 * @param adapter
	 * */

	public UserController(Adapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * <p>
	 *  A put request is sent to http://localhost:8080/api/user/{userId}, that logic
	 *  is processed and then delivered to the adapter to communicate with the
	 *  DataAccessObject -> the database in turn.
	 *  The response is then rendered a user that the DataAccessObject retrieves.
	 * </p>
	 * @param id
	 * @param user
	 * @return : User
	 * */

	@PutMapping("{id}")
	public @NotNull User updateUser(@RequestBody User user, @PathVariable Long id) throws Exception {
		 return adapter.updateUser(user, id);
	}

	/**
	 * <p>
	 *     A post request is sent to: http://localhost:8080/api/user in means of
	 * 	   starting a request to add a user to the data base. Given there is a new user to be added and given
	 * 	   access to the system, after adding their username and password then they should
	 * 	   be added to the system and client should see their account information show up in the response.
	 * </p>
	 * @param user -> requires a user @RequestBody param in order to create a new User in the database.
	 * @return : userRepository -> saving created User.
	 * */

	@PostMapping
	public @NotNull Optional<User> createUser(@RequestBody User user) {
		try{

			return Optional.of(adapter.createUser(user));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return Optional.empty();
		}
	}

	/**
	 * <p>
	 *     A get request is sent to http://localhost:8080/api/user in means of starting a request to
	 *     retrieve a lsit of all users. It sent sent to the adapter which returns the list of all companies.
	 * </p>
	 * @return : List<User>
	 * */

	@GetMapping
	public @NotNull List<User> getUsers() {
		return adapter.findAll();
	}

	/**
	 * <p>
	 *   A get request is sent to: http://localhost:8080/api/user/{userId} in order to
	 * 	 retrieve data about a single user. The controller process the request sends it to the
	 * 	 adapter. After the request has been validated and processed the specific user is returned.
	 * </p>
	 * @param id -> requires an id @PathVariable in order to search for the requested user.
	 * @return: User
	 * */

	@GetMapping("{id}")
	public @NotNull	User getUser(@PathVariable Long id){
		return adapter.findUser(id);
	}
}