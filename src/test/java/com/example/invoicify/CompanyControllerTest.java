package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.CompanyController;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.adapter.DuplicateCompanyException;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.json.JSONArray;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(CompanyController.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private CompanyController companyController;

    private Adapter adapter;

    private CompanyDataAccess companyDataAccess;

    @AfterEach
    public void resetMocks() {
        Mockito.reset(this.companyRepository);
    }

    @BeforeAll
    public void createAdapter() {

        this.companyRepository = Mockito.mock(CompanyRepository.class);

        this.adapter = new Adapter(userRepository, companyRepository, passwordEncoder);

        this.companyController = new CompanyController(adapter);

    }

    @Test
    public void testViewAllCompanies() throws Exception {

        // Instantiating an object mapper instance
        final ObjectMapper objectMapper = new ObjectMapper();

        // Creating a new CompanyDataAccess ArrayList with setting an Id and name
        final ArrayList<CompanyDataAccess> companyDataAccesses = new ArrayList<CompanyDataAccess>() {{
            CompanyDataAccess companyDataAccess = new CompanyDataAccess();
            companyDataAccess.setId(1L);
            companyDataAccess.setName("LTI");

            // Creating another CompanyDataAccess instance set to the same Id and name
            CompanyDataAccess companyDataAccess2 = new CompanyDataAccess();
            companyDataAccess2.setId(1L);
            companyDataAccess2.setName("LTI");

        }};

        // Setting the list of companies to a expected variable, streams the result, and maps all CompanyDataAccess instances,
        //      then converts to a company model object, finally persists to a list
        List<Company> expectedCompanies =
                companyDataAccesses
                        .stream()
                        .map((companyDataAccess -> companyDataAccess.convertToModel(Company::new)))
                        .collect(Collectors.toList());

        // Utilizing company repository to find all companyDataAccesses
        when(companyRepository.findAll()).thenReturn(companyDataAccesses);

        // This allows the controller, adapter, data access, and model to work as expected
        // ONLY the repository is hardcoded for its response
        System.out.println(adapter._companyRepository.findAll().size());

        // Setting an Optional List of Companies to an actual variable that calls the companyController to view all companies
        final Optional<List<Company>>
                actualCompanyListOptional = this.companyController.viewAllCompanies();

        // Assume that the list of companies is present
        assertTrue(actualCompanyListOptional.isPresent());

        // Unwrapping the optional list of companies
        final List<Company> actualCompanyList = actualCompanyListOptional.get();

        Assertions.assertEquals(
                expectedCompanies.size(),
                actualCompanyList.size()
        ); // size of list the same

        for (int i = 0; i < actualCompanyList.size(); i++) // compares json strings of response to expected for each company
            Assertions.assertEquals(
                    objectMapper.writeValueAsString(expectedCompanies.get(i)), // converts company object to json string
                    objectMapper.writeValueAsString(actualCompanyList.get(i)) // extracts json string from json array
            );

        // Making sure that the company repository found 2 entries
        verify(companyRepository, times(2)).findAll();

    }

    @Test
    public void testGetCompanyById() throws Exception {

        // Instantiating an object mapper instance
        final ObjectMapper objectMapper = new ObjectMapper();

        // Creating a CompanyDataAccess instance and setting the Id and name
        CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setId(1L);
        companyDataAccess.setName("LTI");

        // Setting a companyDataAccess object variable then converting it to a company object
        final Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        // Utilizing company repository to find one corresponding companyDataAccess by Id
        when(companyRepository.findById(companyDataAccess.getId())).thenReturn(Optional.of(companyDataAccess));

        // Setting an Optional Company to an actual variable that calls the companyController to find one specific company by Id
        final Optional<Company> actualCompanyOptional = this.companyController.findById(companyDataAccess.getId());

        // Assume that the company is present
        assertTrue(actualCompanyOptional.isPresent());

        // Unwrapping the optional company
        final Company actualCompany = this.companyController.findById(companyDataAccess.getId()).get();

        // Makes sure the Ids of the retrieved companies are the same
        Assertions.assertEquals(
                objectMapper.writeValueAsString(expectedCompany.getId()),
                objectMapper.writeValueAsString(actualCompany.getId())
        );

    }

    @Test
    public void testCreateCompanyThatAlreadyExists() throws Exception {

        // Creating a CompanyDataAccess instance and setting the name
        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName("Subway");

        // Setting a companyDataAccess object variable then converting it to a company object
        final Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        // when for bad case (name is not unique)
        when(this.companyRepository.findByName(companyDataAccess.getName())).thenReturn(Optional.of(companyDataAccess));

        // test bad case (name is not unique)
        assertFalse(this.companyController.addCompany(expectedCompany).isPresent());

    }

    @Test
    public void testAddCompany() throws Exception {

        // Instantiating an object mapper instance
        final ObjectMapper objectMapper = new ObjectMapper();

        // Creating a CompanyDataAccess instance and setting the name
        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName("Subway");

        // Setting a companyDataAccess object variable then converting it to a company object
        final Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        // When for good case (name is unique)
        when(this.companyRepository.findByName(companyDataAccess.getName())).thenReturn(Optional.empty());

        // Testing to ensure that persisting to the company table is working
        when(companyRepository.save(companyDataAccess)).thenReturn(companyDataAccess);

        // Sets an Optional Company object variable that calls companyController to add a company
        final Optional<Company> actualCompany = this.companyController.addCompany(expectedCompany);

        // Assume that the company is present
        assertTrue(actualCompany.isPresent());

        // Makes sure that the two companies are the same
        assertEquals(
                objectMapper.writeValueAsString(expectedCompany),
                objectMapper.writeValueAsString(actualCompany.get())

        );

    }


    @Test
    public void testDeleteCompanyById() throws Exception {

        // Instantiating an object mapper instance
        final ObjectMapper objectMapper = new ObjectMapper();

        // Creating a CompanyDataAccess instance and setting the name and Id
        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();

        companyDataAccess.setName("LTI");
        companyDataAccess.setId(1L);

        // Setting a companyDataAccess object variable then converting it to a company object
        final Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        // Utilizing company repository to find one corresponding companyDataAccess by Id
        when(this.companyRepository.findById(companyDataAccess.getId())).thenReturn(Optional.of(companyDataAccess));

        // Setting an Optional Company to an actual variable that calls the companyController to find one specific company by Id and delete it
        final Optional<Company> actualCompany = this.companyController.deleteCompanyById(expectedCompany.getId());

        // Assume that the company is present
        assertTrue(actualCompany.isPresent());

        // Adds when to handle the company repository's method call to delete an object.
        // to test if an object is actually be deleted from the database in this test file
        doAnswer(invocationOnMock -> {
            final Long toDelete = invocationOnMock.getArgument(0, Long.class);
            assertEquals(companyDataAccess.getId(), toDelete);
            return null;
        })
                .when(this.companyRepository).deleteById(1L);

        // Ensures that the deleted company matches the Id and name of the company that the user is attempting to delete,
        //  if not, then throws a RuntimeException
        assertEquals(
                objectMapper.writeValueAsString(expectedCompany),
                objectMapper.writeValueAsString(actualCompany.orElseThrow(RuntimeException::new))

        );

    }

    @Test
    public void testModifyCompanyName() throws Exception {

        // Creating two CompanyDataAccess instances and setting the Id and name
        final CompanyDataAccess existingCompanyToBeUpdated = new CompanyDataAccess(1L, "LTI");
        final CompanyDataAccess savedUpdatedCompany = new CompanyDataAccess(1L, "LTI2");

        // Creating a Company model instance to the name of the object that is being modified
        final Company expectedCompany = new Company(1L, "LTI2");

        // Utilizing company repository to find one corresponding companyDataAccess by Id
        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompanyToBeUpdated));

        // Testing to ensure that the modified company has been added to the table
        when(companyRepository.save(any())).thenReturn(savedUpdatedCompany);

        // Setting an Optional Company to an actual variable that calls the companyController to find that company and modify it
        final Optional<Company> actualCompanyOptional = companyController.updateCompany(expectedCompany, 1L);

        // Assume that the company is present
        assertTrue(actualCompanyOptional.isPresent());

        // Unwrapping the optional company
        final Company actualCompany = actualCompanyOptional.get();

        // Ensures that the modified company is a new object
        assertEquals(actualCompany.getName(), "LTI2");
        assertEquals(actualCompany.getId(), 1L);

        // Verifies that the company has been found, and added back to the table
        verify(companyRepository, times(1)).findById(any());
        verify(companyRepository, times(1)).save(any());

    }

}











