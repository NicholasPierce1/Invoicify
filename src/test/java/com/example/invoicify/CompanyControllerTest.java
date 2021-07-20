package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.CompanyController;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.adapter.DuplicateCompanyException;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

//@WebMvcTest(CompanyController.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    private FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    private RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    private CompanyRepository companyRepository;

    private UserRepository _userRepository;

    @Autowired
    private PasswordEncoder _passwordEncoder;

    private CompanyController companyController;

    private Adapter adapter;

    @AfterEach
    public void resetMocks(){
        Mockito.reset(this.companyRepository);
    }

    @BeforeAll
    public void createAdapter(){

        this.companyRepository = Mockito.mock(CompanyRepository.class);

        this.adapter = new Adapter(
                _userRepository,
                companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _passwordEncoder);

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
        final List<Company> actualCompanyList = this.companyController.viewAllCompanies();

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

        Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        when(companyRepository.findById(companyDataAccess.getId())).thenReturn(Optional.of(companyDataAccess));

        final Company actualCompany = this.companyController.findById(companyDataAccess.getId());

        Assertions.assertEquals(
                objectMapper.writeValueAsString(expectedCompany.getId()),
                objectMapper.writeValueAsString(actualCompany.getId())
        );


    }

    @Test
    public void testAddCompany() throws Exception {

        final ObjectMapper objectMapper = new ObjectMapper();

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        final CompanyDataAccess companyDataAccess2 = new CompanyDataAccess();

        companyDataAccess.setName("LTI");

        companyDataAccess2.setName("Subway");

        Company expectedCompany =
                companyDataAccess.convertToModel(Company::new);

        Company expectedCompany2 =
                companyDataAccess2.convertToModel(Company::new);

        when(companyRepository.save(companyDataAccess)).thenReturn(companyDataAccess);

        // when for good case (name is unique)
        when(this.companyRepository.findByName(companyDataAccess.getName())).thenReturn(Optional.empty());

        // when for bad case (name is not unique)
        when(this.companyRepository.findByName(companyDataAccess2.getName())).thenReturn(Optional.of(companyDataAccess2));

//        final Company actualCompany = this.companyController.addCompany(expectedCompany);
        final Company actualCompany = this.companyController.addCompany(expectedCompany);

        assertEquals(

                objectMapper.writeValueAsString(actualCompany),
                objectMapper.writeValueAsString(expectedCompany)

        );

       // test bad case (name is not unique)
        assertThrows(
                DuplicateCompanyException.class,
                () ->{
                    this.companyController.addCompany(expectedCompany2);
                }
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

        CompanyDataAccess existingCompanyToBeUpdated = new CompanyDataAccess(1L,"LTI");
        CompanyDataAccess savedUpdatedCompany = new CompanyDataAccess(1L,"LTI2");

        Company expectedCompany= new Company(1L,"LTI2");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompanyToBeUpdated));
        when(companyRepository.save(any())).thenReturn(savedUpdatedCompany);

        Company actualCompany = companyController.updateCompany( expectedCompany, 1L);

        assertEquals(actualCompany.getName(), "LTI2");
        assertEquals(actualCompany.getId(), 1L);

        verify(companyRepository, times(1)).findById(any());
        verify(companyRepository, times(1)).save(any());


    }

}











