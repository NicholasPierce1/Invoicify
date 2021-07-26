package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.repository.adapter.Adapter;
import com.sun.istack.NotNull;
import org.springframework.web.bind.annotation.*;
import com.galvanize.invoicify.models.User;
import java.util.List;
import java.util.Optional;

/**
 * <h2>
 *     	The UserController class is responsible for facilitating
 * 	   	business logic to the adapter to create a response entity.
 * 	   	It receives requests and based upon the endpoint
 * 	   	it will return to the client a list of users, individual users, or it will perform
 * 	  	a create, delete, or update CRUD action (implemented from JPA Repository)
 * </h2>
 */

@RestController
@RequestMapping("/api/user/")
public final class UserController {

	/**
	 * <p>
	 *		The adapter is a connector between the IConvertible and IDataAcess
	 *		interfaces that connects the modelObject to UserDataAccess.
	 * </p>
	 * */

	private final Adapter adapter;

	/**
	 * <p>
	 *		This is the constructor that takes in the Adapter bean
	 *		and renders a bean of type UserController.
	 * </p>
	 * @param adapter a preexisting bean injection servicing a remote data store.
	 * */

	public UserController(Adapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * <p>
	 *  	A Put request is sent to http://localhost:8080/api/user/{userId}, the request invokes
	 *  	the adapter to confirm the user
	 *  	DataAccessObject -> the database in turn.
	 *  	The response is then rendered a user that the DataAccessObject retrieves.
	 * </p>
	 * @param id -> requires an id in the @PathVariable to locate the user to update.
	 * @param user -> requires a User type user from the request body prior to updating a user
	 * @return : User
	 */

	@PutMapping("{id}")
	public @NotNull Optional<User> updateUser(@RequestBody User user, @PathVariable Long id)  {
		 try {
		 	return Optional.of(adapter.updateUser(user, id));
		 } catch (Exception e) {
			 System.out.println(e.getLocalizedMessage());
			 return Optional.empty();
		 }
	}

	/**
	 * <p>
	 *     A post request is sent to: http://localhost:8080/api/user in means of
	 * 	   starting a request to add a user to the data base. Given there is a new user to be added and given
	 * 	   access to the system, after adding their username and password then they should
	 * 	   be added to the system and client should see their account information show up in the response.
	 * </p>
	 * @param user -> requires a user @RequestBody param in order to create a new User in the database.
	 * @return : Optional<User> : a user is returned once that User is created
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
	 * @return : List<User> : a complied list of all present users
	 * */

	@GetMapping
	public @NotNull Optional<List<User>> getUsers() {
		try {
			return Optional.of(adapter.findAll());
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		} return Optional.empty();
	}

	/**
	 * <p>
	 *   	A get request is sent to: http://localhost:8080/api/user/{userId} in order to
	 * 	 	retrieve data about a single user. The controller process the request sends it to the
	 * 	 	adapter. After the request has been validated and processed the specific user is returned.
	 * </p>
	 * @param id -> requires an id @PathVariable in order to search for the requested user.
	 * @return: User
	 * */

	@GetMapping("{id}/")
	public @NotNull	User getUser(@PathVariable Long id) {
		return adapter.findUser(id);
	}
}