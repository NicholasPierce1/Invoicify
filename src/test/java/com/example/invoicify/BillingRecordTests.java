package com.example.invoicify;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.BillingRecordController;
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

    @Autowired
    private ObjectMapper _objectMapper;

    private UserDataAccess _userOneDataAccess;

    private CompanyDataAccess _companyOneDataAccess;

    @BeforeAll
    public void createAdapter(){

        // creating mocked instances to register pre-determined outputs on scoped interactions for each test
        this._flatFeeBillingRecordRepository = Mockito.mock(FlatFeeBillingRecordRepository.class);
        this._rateBasedBillingRecordRepository = Mockito.mock(RateBaseBillingRecordRepository.class);
        this._userRepository = Mockito.mock(UserRepository.class);
        this._companyRepository = Mockito.mock(CompanyRepository.class);

        // initializes the adapter that will be maintained locally to
        // ensure the integrity and invocation/usage of test stub mocks
        this._adapter = new Adapter(
                _userRepository,
                _companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _passwordEncoder);

        this._billingRecordController = new BillingRecordController(_adapter);

        // creates hardcoded companies and users
        // subsequently converts to reflected data access equivalency for pending test mocks
        final Company companyOne = new Company();
        companyOne.setName("Subway");
        companyOne.setId(1L);

        this._companyOneDataAccess = new CompanyDataAccess();
        this._companyOneDataAccess.setName(companyOne.getName());
        this._companyOneDataAccess.setId(companyOne.getId());

        final User userOne = new User();
        userOne.setId(1L);
        userOne.setUsername("username");
        userOne.setPassword("password");

        this._userOneDataAccess = new UserDataAccess();
        this._userOneDataAccess.setId(userOne.getId());
        this._userOneDataAccess.setUsername(userOne.getUsername());
        this._userOneDataAccess.setPassword(userOne.getPassword());

        // initializes flat fee billing records & tailors corresponding user and companies to them
        // to fully qualify the data access objects
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

        // initializes rate based billing records & tailors corresponding user and companies to them
        // to fully qualify the data access objects
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

        // streams flat fees in order to map them to their corresponding model objects
        this._flatFeeBillingRecords = this._flatFeeBillingRecordsDataAccess
                .stream()
                .map(
                        (flatFeeBillingRecordDataAccess -> flatFeeBillingRecordDataAccess.convertToModel(FlatFeeBillingRecord::new))
                )
                .collect(Collectors.toList());

        // streams rate base fees in order to map them to their corresponding model objects
        this._rateBasedBillingRecords = this._rateBasedBillingRecordsDataAccess
                .stream()
                .map(
                        (flatFeeBillingRecordDataAccess -> flatFeeBillingRecordDataAccess.convertToModel(RateBasedBillingRecord::new))
                )
                .collect(Collectors.toList());

        // collates the previous model objects together into their upcasted model object
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
            when(this._userRepository.findById(flatFee.getCreatedBy())).thenReturn(Optional.of(this._userOneDataAccess));
            when(this._companyRepository.findById(flatFee.getCompanyId())).thenReturn(Optional.of(this._companyOneDataAccess));
            when(this._flatFeeBillingRecordRepository.findById(flatFee.getId())).thenReturn(Optional.of(flatFee));

            // registers null mocks for rate based (rate based ids should yield nullO
            when(this._rateBasedBillingRecordRepository.findById(flatFee.getId())).thenReturn(Optional.empty());
        }

        for(final RateBasedBillingRecordDataAccess rateBasedFee : this._rateBasedBillingRecordsDataAccess){
            when(this._userRepository.findById(rateBasedFee.getCreatedBy())).thenReturn(Optional.of(this._userOneDataAccess));
            when(this._companyRepository.findById(rateBasedFee.getCompanyId())).thenReturn(Optional.of(this._companyOneDataAccess));
            when(this._rateBasedBillingRecordRepository.findById(rateBasedFee.getId())).thenReturn(Optional.of(rateBasedFee));
        }

    }

    @AfterEach
    public void resetMocks(){
        Mockito.reset(
                this._flatFeeBillingRecordRepository,
                this._rateBasedBillingRecordRepository,
                this._companyRepository,
                this._userRepository
        );

        Mockito.clearInvocations(
                this._flatFeeBillingRecordRepository,
                this._rateBasedBillingRecordRepository,
                this._companyRepository,
                this._userRepository
        );
    }

    @Test
    public void testGetAllBillingRecords() throws Exception{

        // acquires actual list from controller invocation
        final Optional<List<BillingRecord>> actualBillingRecordsOptional = this._billingRecordController.getAllBillingRecords();

        // asserts that optional is non-empty
        assertTrue(actualBillingRecordsOptional.isPresent());

        final List<BillingRecord> actualBillingRecords = actualBillingRecordsOptional.get();

        // assert size of lists are equal
        assertEquals(this._generalBillingRecordAmalgamation.size(), actualBillingRecords.size());

        // assert list content is of expected (note: is order specific to implementation)
        assertEquals(
                this._objectMapper.writeValueAsString(this._generalBillingRecordAmalgamation),
                this._objectMapper.writeValueAsString(actualBillingRecords)
        );

        // verifies that flat fee and rate base repos initiated request to find all one time
        // (regardless of children requests for child state)
        verify(this._flatFeeBillingRecordRepository, times(1)).findAll();

        verify(this._rateBasedBillingRecordRepository, times(1)).findAll();

        // verifies each child repository invoked for hibernate retrieval for n-objects
        verify(
                this._companyRepository,
                times(this._generalBillingRecordAmalgamation.size())
        )
                .findById(this._companyOneDataAccess.getId());

        verify(
                this._userRepository,
                times(this._generalBillingRecordAmalgamation.size())
        )
                .findById(this._userOneDataAccess.getId());

        // asserts no subsequent interactions with any mocked instances -- includes invocation queue
        verifyNoMoreInteractions(
                this._rateBasedBillingRecordRepository,
                this._flatFeeBillingRecordRepository,
                this._userRepository,
                this._companyRepository
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

        // verifies rate based retrieves corresponding objects via id & flat fee returns null for every id
        for(final RateBasedBillingRecordDataAccess rateBased : this._rateBasedBillingRecordsDataAccess) {
            verify(
                    this._rateBasedBillingRecordRepository,
                    times(1)
            ).findById(rateBased.getId());

            verify(
                    this._flatFeeBillingRecordRepository,
                    times(1)
            ).findById(rateBased.getId());
        }

        // verifies every flat fee has been found by its ID only 1 time
        for(final FlatFeeBillingRecordDataAccess flatFee : this._flatFeeBillingRecordsDataAccess)
            verify(
                    this._flatFeeBillingRecordRepository,
                    times(1)
            ).findById(flatFee.getId());

        // verifies each child received n-requests to retrieve its state
        // from its id
        verify(
                this._companyRepository,
                times(this._generalBillingRecordAmalgamation.size())
        )
                .findById(this._companyOneDataAccess.getId());

        verify(
                this._userRepository,
                times(this._generalBillingRecordAmalgamation.size())
        )
                .findById(this._userOneDataAccess.getId());

        // asserts no subsequent interactions with any mocked instances -- includes invocation queue
        verifyNoMoreInteractions(
                this._flatFeeBillingRecordRepository,
                this._userRepository,
                this._companyRepository
        );

    }

    @Test
    public void testAddNewFlatFee() throws Exception {

        // creates flat fee w/o id
        final FlatFeeBillingRecord flatFeeBillingRecord = this._flatFeeBillingRecords.get(0);
        flatFeeBillingRecord.setId(null);

        final FlatFeeBillingRecordDataAccess flatFeeBillingRecordDataAccess = this._flatFeeBillingRecordsDataAccess.get(0);

        final FlatFeeBillingRecord expectedFlatFeeBillingRecord = flatFeeBillingRecordDataAccess.convertToModel(FlatFeeBillingRecord::new);

        // registers save mock & pre-req mocks
        when(
                this._flatFeeBillingRecordRepository
                        .save(any(FlatFeeBillingRecordDataAccess.class))
        )
                .thenReturn(flatFeeBillingRecordDataAccess);

        when(
                this._companyRepository.findById(flatFeeBillingRecord.getClient().getId())
        )
                .thenReturn(Optional.of(this._companyOneDataAccess));

        when(
                this._userRepository.findById(flatFeeBillingRecord.getCreatedBy().getId())
        )
                .thenReturn(Optional.of(this._userOneDataAccess));

        assertEquals(
                this._objectMapper.writeValueAsString(expectedFlatFeeBillingRecord),
                this._objectMapper.writeValueAsString(
                        this._billingRecordController.saveFlatFeeBillingRecord(flatFeeBillingRecord)
                                .orElseThrow(RuntimeException::new)
                )
        );

        verify(this._flatFeeBillingRecordRepository, times(1))
                .save(any(FlatFeeBillingRecordDataAccess.class));

        verify(this._userRepository, times(1))
                .findById(flatFeeBillingRecord.getCreatedBy().getId());

        verify(this._companyRepository, times(1))
                .findById(flatFeeBillingRecord.getClient().getId());

        verifyNoMoreInteractions(
                this._flatFeeBillingRecordRepository,
                this._companyRepository,
                this._userRepository
        );
    }

    @Test
    public void testAddNewRateBasedFee() throws Exception {

        // creates rate based fee w/o id
        final RateBasedBillingRecord rateBasedBillingRecord = this._rateBasedBillingRecords.get(0);
        rateBasedBillingRecord.setId(null);

        final RateBasedBillingRecordDataAccess rateBasedBillingRecordDataAccess = this._rateBasedBillingRecordsDataAccess.get(0);

        final RateBasedBillingRecord expectedRateBaseBillingRecord = rateBasedBillingRecordDataAccess.convertToModel(RateBasedBillingRecord::new);

        // registers save mock & pre-req mocks
        when(
                this._rateBasedBillingRecordRepository
                        .save(any(RateBasedBillingRecordDataAccess.class))
        )
                .thenReturn(rateBasedBillingRecordDataAccess);

        when(
                this._companyRepository.findById(rateBasedBillingRecord.getClient().getId())
        )
                .thenReturn(Optional.of(this._companyOneDataAccess));

        when(
                this._userRepository.findById(rateBasedBillingRecord.getCreatedBy().getId())
        )
                .thenReturn(Optional.of(this._userOneDataAccess));

        // asserts equality amongst the JSON stings of the fully, qualified rate base record that has been saved
        assertEquals(
                this._objectMapper.writeValueAsString(expectedRateBaseBillingRecord),
                this._objectMapper.writeValueAsString(
                        this._billingRecordController.saveRateBasedBillingRecord(rateBasedBillingRecord)
                                .orElseThrow(RuntimeException::new)
                )
        );

        // verify that the rate base billing record has been saved once
        verify(this._rateBasedBillingRecordRepository, times(1))
                .save(any(RateBasedBillingRecordDataAccess.class));

        // verifies children have invoked for full, qualification collation occured once
        verify(this._userRepository, times(1))
                .findById(rateBasedBillingRecord.getCreatedBy().getId());

        verify(this._companyRepository, times(1))
                .findById(rateBasedBillingRecord.getClient().getId());

        // asserts no subsequent interactions with any mocked instances -- includes invocation queue
        verifyNoMoreInteractions(
                this._rateBasedBillingRecordRepository,
                this._companyRepository,
                this._userRepository
        );

    }

}
