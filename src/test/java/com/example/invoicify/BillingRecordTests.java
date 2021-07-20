package com.example.invoicify;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.BillingRecordController;
import com.galvanize.invoicify.controllers.CompanyController;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.aspectj.lang.annotation.After;
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

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BillingRecordTests {

    private FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    private RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    private CompanyRepository _companyRepository;

    private UserRepository _userRepository;

    @Autowired
    private PasswordEncoder _passwordEncoder;

    private BillingRecordController _billingRecordController;

    private Adapter _adapter;

    private List<FlatFeeBillingRecord> _flatFeeBillingRecords;

    private List<FlatFeeBillingRecordDataAccess> _flatFeeBillingRecordsDataAccess;

    private List<RateBasedBillingRecord> _rateBasedBillingRecords;

    private List<RateBasedBillingRecordDataAccess> _rateBasedBillingRecordsDataAccess;

    private List<BillingRecord> _generalBillingRecordAmalgamation;

    private ObjectMapper _objectMapper;

    private UserDataAccess userOneDataAccess;

    private CompanyDataAccess companyOneDataAccess;

    @BeforeAll
    public void createAdapter(){

        this._objectMapper = new ObjectMapper();

        this._flatFeeBillingRecordRepository = Mockito.mock(FlatFeeBillingRecordRepository.class);
        this._rateBasedBillingRecordRepository = Mockito.mock(RateBaseBillingRecordRepository.class);
        this._userRepository = Mockito.mock(UserRepository.class);
        this._companyRepository = Mockito.mock(CompanyRepository.class);

        this._adapter = new Adapter(
                _userRepository,
                _companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _passwordEncoder);

        this._billingRecordController = new BillingRecordController(_adapter);

        final Company companyOne = new Company();
        companyOne.setName("Subway");
        companyOne.setId(1L);

        this.companyOneDataAccess = new CompanyDataAccess();
        this.companyOneDataAccess.setName(companyOne.getName());
        this.companyOneDataAccess.setId(companyOne.getId());

        final User userOne = new User();
        userOne.setId(1L);
        userOne.setUsername("username");
        userOne.setPassword("password");

        this.userOneDataAccess = new UserDataAccess();
        this.userOneDataAccess.setId(userOne.getId());
        this.userOneDataAccess.setUsername(userOne.getUsername());
        this.userOneDataAccess.setPassword(userOne.getPassword());

        this._flatFeeBillingRecordsDataAccess = new ArrayList<FlatFeeBillingRecordDataAccess>()
        {{
            add(new FlatFeeBillingRecordDataAccess()
                {{
                    setId(1L);
                    setAmount(150);
                    setDescription("description one");
                    setCompanyId(companyOne.getId());
                    setCompany(companyOne);
                    setCreatedBy(userOne.getId());
                    setUser(userOne);
                    setInUse(false);
                }}
            );

            add(new FlatFeeBillingRecordDataAccess()
                {{
                    setId(2L);
                    setAmount(125.5);
                    setCompanyId(companyOne.getId());
                    setCompany(companyOne);
                    setCreatedBy(userOne.getId());
                    setUser(userOne);
                    setDescription("description two");
                    setInUse(true);
                }}
            );
        }};

        this._rateBasedBillingRecordsDataAccess = new ArrayList<RateBasedBillingRecordDataAccess>()
        {{
            add(new RateBasedBillingRecordDataAccess()
                {{
                    setId(3L);
                    setCompanyId(companyOne.getId());
                    setCompany(companyOne);
                    setCreatedBy(userOne.getId());
                    setUser(userOne);
                    setDescription("description one");
                    setInUse(false);
                    setQuantity(10);
                    setRate(15);
                }}
            );

            add(new RateBasedBillingRecordDataAccess()
                {{
                    setId(4L);
                    setCompanyId(companyOne.getId());
                    setCompany(companyOne);
                    setCreatedBy(userOne.getId());
                    setUser(userOne);
                    setDescription("description two");
                    setInUse(false);
                    setQuantity(5);
                    setRate(5.63);
                }}
            );
        }};

        this._flatFeeBillingRecords = this._flatFeeBillingRecordsDataAccess
                .stream()
                .map(
                        (flatFeeBillingRecordDataAccess -> flatFeeBillingRecordDataAccess.convertTo(FlatFeeBillingRecord::new))
                )
                .collect(Collectors.toList());

        this._rateBasedBillingRecords = this._rateBasedBillingRecordsDataAccess
                .stream()
                .map(
                        (flatFeeBillingRecordDataAccess -> flatFeeBillingRecordDataAccess.convertTo(RateBasedBillingRecord::new))
                )
                .collect(Collectors.toList());

        this._generalBillingRecordAmalgamation = new ArrayList<BillingRecord>(){{
            addAll(_flatFeeBillingRecords);
            addAll(_rateBasedBillingRecords);
        }};

    }

    @BeforeEach
    public void setSharedMocks(){

        // registers mock on flat fee & rate base repositories for their find all stubs
        when(this._flatFeeBillingRecordRepository.findAll()).thenReturn(this._flatFeeBillingRecordsDataAccess);
        when(this._rateBasedBillingRecordRepository.findAll()).thenReturn(this._rateBasedBillingRecordsDataAccess);

        // registers mock on all id values for every data access
        for(final FlatFeeBillingRecordDataAccess flatFee : this._flatFeeBillingRecordsDataAccess) {
            when(this._userRepository.findById(flatFee.getCreatedBy())).thenReturn(Optional.of(this.userOneDataAccess));
            when(this._companyRepository.findById(flatFee.getCompanyId())).thenReturn(Optional.of(this.companyOneDataAccess));
            when(this._flatFeeBillingRecordRepository.findById(flatFee.getId())).thenReturn(Optional.of(flatFee));
        }

        for(final RateBasedBillingRecordDataAccess rateBasedFee : this._rateBasedBillingRecordsDataAccess){
            when(this._userRepository.findById(rateBasedFee.getCreatedBy())).thenReturn(Optional.of(this.userOneDataAccess));
            when(this._companyRepository.findById(rateBasedFee.getCompanyId())).thenReturn(Optional.of(this.companyOneDataAccess));
            when(this._rateBasedBillingRecordRepository.findById(rateBasedFee.getId())).thenReturn(Optional.of(rateBasedFee));
        }

    }

    @AfterEach
    public void resetMocks(){
        Mockito.reset(
                this._flatFeeBillingRecordRepository,
                this._rateBasedBillingRecordRepository
        );
    }

    @Test
    public void testGetAllBillingRecords() throws Exception{

        // acquires actual list from controller invocation
        final List<BillingRecord> actualBillingRecords = this._billingRecordController.getAllBillingRecords();

        // assert size of lists are equal
        assertEquals(_generalBillingRecordAmalgamation.size(), actualBillingRecords.size());

        // assert list content is of expected (note: is order specific to implementation)
        assertEquals(
                this._objectMapper.writeValueAsString(this._generalBillingRecordAmalgamation),
                this._objectMapper.writeValueAsString(actualBillingRecords)
        );

    }

    @Test
    public void testGetCompanyById() throws Exception{

        // iterates over all billing records
        for(final BillingRecord billingRecord : this._generalBillingRecordAmalgamation){

            // acquires billing record from mocked billing record controller and asserts equality
            assertEquals(
                    this._objectMapper.writeValueAsString(billingRecord),
                    this._objectMapper.writeValueAsString(
                            this._billingRecordController
                                    .getBillingRecordById(billingRecord.getId())
                                    .orElseThrow(RuntimeException::new) // note: will never happen
                    )
            );
        }

    }

}
