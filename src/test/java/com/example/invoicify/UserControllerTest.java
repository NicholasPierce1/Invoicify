package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.UserController;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;

import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class UserControllerTest {

    @Autowired
    PasswordEncoder encoder;

    private UserRepository userRepository;

    private UserController userController;

    private Adapter adapter;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void createAdapter() {
        this.userRepository = Mockito.mock(UserRepository.class);
        adapter = new Adapter(userRepository, encoder);
        this.userController = new UserController(adapter);
    }

    @AfterEach
    public void resetMocks(){
        Mockito.reset(this.userRepository);
    }



    @Test
    public void createAnExistingUser() throws Exception {

        UserDataAccess userDataAccess = new UserDataAccess("testuser1","testpassword2");
        userDataAccess.setUsername("testuser1");

        // Setting a userDataAccess object variable then converting it to a company object
        final User expectedUser =
                userDataAccess.convertToModel(User::new);

        // when for bad case (name is not unique)
        when(this.userRepository.findByUsername(userDataAccess.getUsername())).thenReturn(Optional.of(userDataAccess));

        // test bad case (name is not unique)
        assertFalse(this.userController.createUser(expectedUser).isPresent());

        verify(userRepository, times(1)).findByUsername(any());
        verifyNoMoreInteractions(this.userRepository);
    }


    @Test
    public void createNewUser() throws Exception {
        UserDataAccess userDataAccess = new UserDataAccess("testuser1","testpassword2");
        User expectedUser = new User(userDataAccess.getUsername(),userDataAccess.getPassword());

        when(userRepository.save(any())).thenReturn(userDataAccess);
        Optional<User> actualUserOptional = this.userController.createUser(expectedUser);
        assertTrue(actualUserOptional.isPresent());
        User actualUser = actualUserOptional.get();


        String actualUserStr = objectMapper.writeValueAsString(actualUser);
        String expectedUserStr = objectMapper.writeValueAsString(expectedUser);

        assertEquals(expectedUserStr,actualUserStr);
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByUsername(any());

        verifyNoMoreInteractions(this.userRepository);

    }



    @Test
    public void getAllUsers() throws Exception {
        UserDataAccess user1 = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess user2 = new UserDataAccess("testuser2", "testpassword2");
        List<UserDataAccess> mockUserDataAccessList = new ArrayList<UserDataAccess>();
        mockUserDataAccessList.add(user1);
        mockUserDataAccessList.add(user2);
        List<User> expectedUsers = mockUserDataAccessList.stream().map(userDataAccess -> userDataAccess.convertToModel(User::new)).collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(mockUserDataAccessList);
        final Optional<List<User>> actualUsersOptional = userController.getUsers();
        assertTrue(actualUsersOptional.isPresent());
        assertTrue(actualUsersOptional.get().size() == 2);

        List<User> actualUsers = actualUsersOptional.get();

        for(int i=0; i<actualUsers.size(); i++) {
            Assertions.assertEquals(
                    objectMapper.writeValueAsString(actualUsers.get(i)),
                    objectMapper.writeValueAsString(expectedUsers.get(i))
            );

        }
        verify(userRepository, times(1)).findAll();

        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void getSpecificUser() throws Exception {
        UserDataAccess userDataAccess = new UserDataAccess("testuser1","testpassword2");
        User expectedUser = new User(userDataAccess.getUsername(),userDataAccess.getPassword());

        when(userRepository.findById(any())).thenReturn(Optional.of(userDataAccess));
        final User actualUser = userController.getUser(1L);

        String actualUserStr = objectMapper.writeValueAsString(actualUser);
        String expectedUserStr = objectMapper.writeValueAsString(expectedUser);
        assertEquals(actualUserStr, expectedUserStr);

        verify(userRepository, times(1)).findById(1L);

        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    public void modifyUserCredentialsWithNonExistingUserNameAndPassword() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess savedUpdatedUser = new UserDataAccess("NewUserName","NewPassword");

        User expectedUser = new User("NewUserName","NewPassword");

        when(userRepository.findById(any())).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.save(any())).thenReturn(savedUpdatedUser);

        Optional<User> actualUserOptional = userController.updateUser(expectedUser, 1L);

        assertTrue(actualUserOptional.isPresent());

        User actualUser = actualUserOptional.get();

        assertEquals(actualUser.getUsername(), expectedUser.getUsername());
        assertEquals(actualUser.getPassword(), expectedUser.getPassword());

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByUsername(any());

        verifyNoMoreInteractions(this.userRepository);

    }


    @Test
    public void modifyUserCredentialsWithJustUserName() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess savedUpdatedUser = new UserDataAccess("NewUserName","testpassword2");

        User expectedUser = new User("NewUserName","");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.save(any())).thenReturn(savedUpdatedUser);

        Optional<User> actualUserOptional = userController.updateUser( expectedUser, 1L);

        assertTrue(actualUserOptional.isPresent());

        User actualUser = actualUserOptional.get();

        assertEquals(actualUser.getUsername(), "NewUserName");
        assertEquals(actualUser.getPassword(), "testpassword2");

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByUsername(any());

        verifyNoMoreInteractions(this.userRepository);

    }

    @Test
    public void modifyUserCredentialsWithJustPassword() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess savedUpdatedUser = new UserDataAccess("testuser1","newPassword1");

        User expectedUser = new User("","newPassword1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.save(any())).thenReturn(savedUpdatedUser);

        Optional<User> actualUserOptional = userController.updateUser( expectedUser, 1L);

        assertTrue(actualUserOptional.isPresent());

        User actualUser = actualUserOptional.get();

        assertEquals(actualUser.getUsername(), "testuser1");
        assertEquals(actualUser.getPassword(), "newPassword1");

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());

        verifyNoMoreInteractions(this.userRepository);

    }

}
