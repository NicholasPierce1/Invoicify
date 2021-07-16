package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(CompanyController.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void testViewAllCompanies() throws Exception {


        // todo: nick, create bean for object mapper via mapper build
        final ObjectMapper objectMapper = new ObjectMapper();

        final ArrayList<CompanyDataAccess> companyDataAccesses = new ArrayList<CompanyDataAccess>() {{

            add(new CompanyDataAccess() {{
                setId(1L);
                setName("LTI");
            }});

            add(new CompanyDataAccess() {{
                setId(2L);
                setName("Galvanize");
            }});

        }};

        List<Company> expectedCompanies =
                companyDataAccesses
                        .stream()
                        .map((companyDataAccess -> companyDataAccess.convertTo(Company::new)))
                        .collect(Collectors.toList());

        when(companyRepository.findAll()).thenReturn(companyDataAccesses);
        // this allows the controller, adapter, data access, and model to work as expected
        // ONLY the repository is hardcoded for its response

        final MvcResult response = this.mockMvc.perform(get("/app/company"))
                .andExpect(status().isOk())
                .andReturn();
        // we have the json response for all companies now

        final JSONArray jsonArray = new JSONArray(response.getResponse().getContentAsString());
        // converted to a json array

        Assertions.assertEquals(
                expectedCompanies.size(),
                jsonArray.length()
        ); // size of list the same

        for (int i = 0; i < jsonArray.length(); i++) // compares json strings of response to expected for each company
            Assertions.assertEquals(
                    objectMapper.writeValueAsString(expectedCompanies.get(i)), // converts company object to json string
                    jsonArray.getString(i) // extracts json string from json array
            );

    }

//    @Test
//    public void testGetAllCompanies() throws Exception {
//        this.mockMvc.perform(get("/api/company"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isNotEmpty());
//    }

    @Test
    public void testGetCompanyById() throws Exception {

        Company company = new Company();
        company.setId(1l);

//        RequestBuilder request = get("/app/company/1");
        this.mockMvc.perform(get("/app/company/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
    }

//    @Test
//    public void testGetCompanyById() throws Exception {
//    }
}











