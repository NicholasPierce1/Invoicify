package com.example.invoicify;

import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.models.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(CompanyController.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    objectmapper

    @Test
    public void  testViewAllCompanies() throws Exception {

//        Company comp = new Company();
        ArrayList<Company> companies = new ArrayList<>();
        companies.add( new Company(1L, "LTI"));
        companies.add( new Company(2L, "Galvanize"));
        System.out.println(companies);
        this.mockMvc.perform(get("/app/company"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }
}

