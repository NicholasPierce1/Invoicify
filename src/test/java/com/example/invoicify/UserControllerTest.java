package com.example.invoicify;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.UserController;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.adapter.DuplicateUserException;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.invoicerepository.InvoiceRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    @Autowired
    private RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    @Autowired
    private InvoiceRepository _invoiceRepository;

    @Autowired
    private CompanyRepository _companyRepository;

    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder _passwordEncoder;

    private UserController userController;

    private Adapter adapter;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void createAdapter() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.adapter = new Adapter(
                userRepository,
                _companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _invoiceRepository,
                _passwordEncoder);
        this.userController = new UserController(adapter);
    }

    @AfterEach
    public void resetMocks(){
        Mockito.reset(this.userRepository);
    }


    @Test
    public void createAnExistingUser() throws Exception {

        UserDataAccess userDataAccess = new UserDataAccess("testuser1","testpassword2");
        User expectedUser = new User(userDataAccess.getUsername(),userDataAccess.getPassword());

        when(userRepository.countUsersByUserName(any())).thenReturn(2);
        when(userRepository.save(any())).thenReturn(userDataAccess);
        assertThrows(DuplicateUserException.class, () -> {
            this.userController.createUser(expectedUser);
        });
        verify(userRepository,times(1)).countUsersByUserName(any());
    }


    @Test
    public void createNewUser() throws Exception {
        UserDataAccess userDataAccess = new UserDataAccess("testuser1","testpassword2");
        User expectedUser = new User(userDataAccess.getUsername(),userDataAccess.getPassword());

        when(userRepository.save(any())).thenReturn(userDataAccess);
        User actualUser = this.userController.createUser(expectedUser);

        String actualUserStr = objectMapper.writeValueAsString(actualUser);
        String expectedUserStr = objectMapper.writeValueAsString(expectedUser);

        assertEquals(actualUserStr, expectedUserStr);
        verify(userRepository, times(1)).save(any());
    }



    @Test
    public void getAllUsers() throws Exception {
        UserDataAccess user1 = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess user2 = new UserDataAccess("testuser2", "testpassword2");
        List<UserDataAccess> mockUserDataAccessList = new ArrayList<UserDataAccess>();
        mockUserDataAccessList.add(user1);
        mockUserDataAccessList.add(user2);
        List<User> expectedUsers = mockUserDataAccessList.stream().map(userDataAccess -> userDataAccess.convertTo(User::new)).collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(mockUserDataAccessList);
        final List<User> actualUsers = userController.getUsers();
        assertTrue(actualUsers.size() == 2);


        for(int i=0; i<actualUsers.size(); i++) {
            String actualUserStr = objectMapper.writeValueAsString(actualUsers.get(i));
            String expectedUserStr = objectMapper.writeValueAsString(expectedUsers.get(i));
            assertEquals(actualUserStr, expectedUserStr);
        }


        verify(userRepository, times(1)).findAll();
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

    }

    @Test
    public void modifyUserCredentialsWithNonExistingUserNameAndPassword() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess savedUpdatedUser = new UserDataAccess("NewUserName","NewPassword");

        User expectedUser = new User("NewUserName","NewPassword");

        when(userRepository.findById(any())).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.save(any())).thenReturn(savedUpdatedUser);

        User actualUser = userController.updateUser(null, expectedUser, 1L);

        assertEquals(actualUser.getUsername(), expectedUser.getUsername());
        assertEquals(actualUser.getPassword(), expectedUser.getPassword());

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());

    }


    @Test
    public void modifyUserCredentialsWithJustUserName() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess savedUpdatedUser = new UserDataAccess("NewUserName","testpassword2");

        User expectedUser = new User("NewUserName","");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.save(any())).thenReturn(savedUpdatedUser);

        User actualUser = userController.updateUser(null, expectedUser, 1L);

        assertEquals(actualUser.getUsername(), "NewUserName");
        assertEquals(actualUser.getPassword(), "testpassword2");

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());

    }

    @Test
    public void modifyUserCredentialsWithJustPassword() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        UserDataAccess savedUpdatedUser = new UserDataAccess("testuser1","newPassword1");

        User expectedUser = new User("","newPassword1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.save(any())).thenReturn(savedUpdatedUser);

        User actualUser = userController.updateUser(null, expectedUser, 1L);

        assertEquals(actualUser.getUsername(), "testuser1");
        assertEquals(actualUser.getPassword(), "newPassword1");

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any());

    }

    @Test
    public void modifyUserCredentialsWithAnotherUsernameThatAlreadyExists() throws Exception {
        UserDataAccess existingUserToBeUpdated = new UserDataAccess("testuser1","testpassword2");
        //bob is already existing in the database so don't do anything
        User expectedUser = new User("bob","newPassword1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUserToBeUpdated));
        when(userRepository.countUsersByUserName(any())).thenReturn(2);
        assertThrows(DuplicateUserException.class, () -> {
            userController.updateUser(null, expectedUser, 1L);
        });

        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).countUsersByUserName(any());
    }

}
