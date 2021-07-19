package com.example.invoicify;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.UserController;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
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
    MockMvc mvc;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
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
        User expectedUser = new User(userDataAccess.getUsername(),userDataAccess.getUsername());

        when(userRepository.findById(any())).thenReturn(Optional.of(userDataAccess));
        final User actualUser = userController.getUser(1L);

        String actualUserStr = objectMapper.writeValueAsString(actualUser);
        String expectedUserStr = objectMapper.writeValueAsString(expectedUser);
        assertEquals(actualUserStr, expectedUserStr);



        verify(userRepository, times(1)).findAll();

    }

    @Test
    public void modifyUserCredentialsWithNonExistingUserNameAndPassword() throws Exception {

        String userCredentials = "{" +
                "    \"username\":\"admin2\"," +
                "    \"password\":\"password2\"" +
                "}";


        MockHttpServletRequestBuilder request = put("/api/user/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userCredentials);

        System.out.println(encoder.encode("password1"));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin2"))
                .andExpect(jsonPath("$.password").value(encoder.encode("password2")));

    }

    @Test
    public void modifyUserCredentialsWithJustUserName() throws Exception {

        String userCredentials = "{" +
                "    \"username\":\"admin2\"," +
                "    \"password\":\"\"" +
                "}";

        MockHttpServletRequestBuilder request = put("/api/user/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userCredentials);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin2"));
    }

    @Test
    public void modifyUserCredentialsWithJustPassword() throws Exception {

        String userCredentials = "{" +
                "    \"username\":\"\"," +
                "    \"password\":\"password2\"" +
                "}";

        MockHttpServletRequestBuilder request = put("/api/user/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userCredentials);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value("password2"));
    }

    @Test
    public void modifyUserCredentialsWithAnotherUsernameThatAlreadyExists() throws Exception {

        //bob already exists. Check @SeedData.java
        String userCredentials = "{" +
                    "\"username\":\"bob\"," +
                    "\"password\":\"password2\"" +
                "}";

        MockHttpServletRequestBuilder request = put("/api/user/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userCredentials);


        this.mvc.perform(request)
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void createNewUser() throws Exception {

        String userCredentials = "{" +
                "\"username\":\"newUser\"," +
                "\"password\":\"password2\"" +
                "}";

        MockHttpServletRequestBuilder request = post("/api/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userCredentials);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    public void createAnExistingUser() throws Exception {

        String userCredentials = "{" +
                "\"username\":\"bob\"," +
                "\"password\":\"password2\"" +
                "}";

        MockHttpServletRequestBuilder request = post("/api/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userCredentials);

        this.mvc.perform(request)
                .andExpect(status().is5xxServerError());
    }





}
