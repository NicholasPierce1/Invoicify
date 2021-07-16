package com.example.invoicify;


import com.galvanize.invoicify.InvoicifyApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
public class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PasswordEncoder encoder;

    @Test
    public void getAllUsers() throws Exception {
                this.mvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void getSpecificUser() throws Exception {
        this.mvc.perform(get("/api/user/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7));
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
