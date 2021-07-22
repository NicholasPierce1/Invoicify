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

    @AfterEach
    public void resetMocks(){
        Mockito.reset(this.companyRepository);
    }

    @BeforeAll
    public void createAdapter(){

        this.companyRepository = Mockito.mock(CompanyRepository.class);

        this.adapter = new Adapter(userRepository, companyRepository, passwordEncoder);

        this.companyController = new CompanyController(adapter);

    }

    @Test
    public void  testViewAllCompanies() throws Exception {


        // todo: nick, create bean for object mapper via mapper build
        final ObjectMapper objectMapper = new ObjectMapper();

        final ArrayList<CompanyDataAccess> companyDataAccesses = new ArrayList<CompanyDataAccess>(){{

            add(new CompanyDataAccess(){{
                setId(1L);
                setName("LTI");
            }});

            add(new CompanyDataAccess(){{
                setId(2L);
                setName("Galvanize");
            }});

        }};

        List<Company> expectedCompanies =
                companyDataAccesses
                        .stream()
                        .map( (companyDataAccess -> companyDataAccess.convertToModel(Company::new)) )
                        .collect(Collectors.toList());

        when(companyRepository.findAll()).thenReturn(companyDataAccesses);
        // this allows the controller, adapter, data access, and model to work as expected
        // ONLY the repository is hardcoded for its response
        System.out.println(adapter._companyRepository.findAll().size());
        final Optional<List<Company>>
                actualCompanyListOptional = this.companyController.viewAllCompanies();

        assertTrue(actualCompanyListOptional.isPresent());

        final List<Company> actualCompanyList = actualCompanyListOptional.get();

        Assertions.assertEquals(
                expectedCompanies.size(),
                actualCompanyList.size()
        ); // size of list the same

        for(int i = 0; i < actualCompanyList.size(); i++) // compares json strings of response to expected for each company
            Assertions.assertEquals(
                    objectMapper.writeValueAsString(expectedCompanies.get(i)), // converts company object to json string
                    objectMapper.writeValueAsString(actualCompanyList.get(i)) // extracts json string from json array
            );
        
        verify(companyRepository, times(2)).findAll();

    }

    @Test
    public void testGetCompanyById() throws Exception {

        final ObjectMapper objectMapper = new ObjectMapper();

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess() {{
            setId(1L);
            setName("LTI");
        }};

        final Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        when(companyRepository.findById(companyDataAccess.getId())).thenReturn(Optional.of(companyDataAccess));

        final Optional<Company> actualCompanyOptional = this.companyController.findById(companyDataAccess.getId());

        assertTrue(actualCompanyOptional.isPresent());

        final Company actualCompany = this.companyController.findById(companyDataAccess.getId()).get();

        Assertions.assertEquals(
                objectMapper.writeValueAsString(expectedCompany.getId()),
                objectMapper.writeValueAsString(actualCompany.getId())
        );

    }

    @Test
    public void testCreateCompanyThatAlreadyExists() throws Exception {

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName("Subway");

        Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        // when for bad case (name is not unique)
        when(this.companyRepository.findByName(companyDataAccess.getName())).thenReturn(Optional.of(companyDataAccess));

        // test bad case (name is not unique)
        assertFalse(
                this.companyController.addCompany(expectedCompany).isPresent()
        );

    }

    @Test
    public void testAddCompany() throws Exception {

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName("Subway");

        Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        // when for good case (name is unique)
        when(this.companyRepository.findByName(companyDataAccess.getName())).thenReturn(Optional.empty());

        when(companyRepository.save(companyDataAccess)).thenReturn(companyDataAccess);

        final Optional<Company> actualCompany = this.companyController.addCompany(expectedCompany);

        assertTrue(
                actualCompany.isPresent()
        );

                final ObjectMapper objectMapper = new ObjectMapper();

        assertEquals(

                objectMapper.writeValueAsString(expectedCompany),
                objectMapper.writeValueAsString(actualCompany.get())

        );

    }


    @Test
    public void testDeleteCompanyById() throws Exception {

        final ObjectMapper objectMapper = new ObjectMapper();

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();

        companyDataAccess.setName("LTI");
        companyDataAccess.setId(1L);

        Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        when(this.companyRepository.findById(companyDataAccess.getId())).thenReturn(Optional.of(companyDataAccess));

        final Optional<Company> actualCompany = this.companyController.deleteCompanyById(expectedCompany.getId());

        assertTrue(actualCompany.isPresent());

        assertEquals(
                objectMapper.writeValueAsString(expectedCompany),
                objectMapper.writeValueAsString(actualCompany.orElseThrow(RuntimeException::new))

        );

    }

    @Test
    public void testModifyCompanyName() throws Exception {

        final CompanyDataAccess existingCompanyToBeUpdated = new CompanyDataAccess(1L,"LTI");
        final CompanyDataAccess savedUpdatedCompany = new CompanyDataAccess(1L,"LTI2");

       final Company expectedCompany= new Company(1L,"LTI2");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompanyToBeUpdated));
        when(companyRepository.save(any())).thenReturn(savedUpdatedCompany);

        final Optional<Company> actualCompanyOptional = companyController.updateCompany( expectedCompany, 1L);

        assertTrue(actualCompanyOptional.isPresent());

        Company actualCompany = actualCompanyOptional.get();

        assertEquals(actualCompany.getName(), "LTI2");
        assertEquals(actualCompany.getId(), 1L);

        // revisit this verify
        verify(companyRepository, times(1)).findById(any());
        verify(companyRepository, times(1)).save(any());

    }

}











