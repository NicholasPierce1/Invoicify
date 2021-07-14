package com.example.invoicify;

import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.models.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(CompanyController.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void  testViewAllCompanies() throws Exception {

        Company comp = new Company();

        this.mockMvc.perform(get("/app/company"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string(comp.getName()));

    }
}

