package com.example.invoicify;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.BillingRecordController;
import com.galvanize.invoicify.controllers.CompanyController;
import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.FlatFeeBillingRecord;
import com.galvanize.invoicify.models.RateBasedBillingRecord;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

    @Autowired
    private CompanyRepository _companyRepository;

    @Autowired
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

    @BeforeAll
    public void createAdapter(){

        this._objectMapper = new ObjectMapper();

        this._flatFeeBillingRecordRepository = Mockito.mock(FlatFeeBillingRecordRepository.class);
        this._rateBasedBillingRecordRepository = Mockito.mock(RateBaseBillingRecordRepository.class);

        this._adapter = new Adapter(
                _userRepository,
                _companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _passwordEncoder);

        this._billingRecordController = new BillingRecordController(_adapter);

        this._flatFeeBillingRecordsDataAccess = new ArrayList<FlatFeeBillingRecordDataAccess>()
        {{
            add(new FlatFeeBillingRecordDataAccess()
                {{
                    setId(1L);
                    setAmount(150);
                    setCompanyId(1L);
                    setDescription("description one");
                    setInUse(false);
                }}
            );

            add(new FlatFeeBillingRecordDataAccess()
                {{
                    setId(2L);
                    setAmount(125.5);
                    setCompanyId(1L);
                    setDescription("description two");
                    setInUse(true);
                }}
            );
        }};

        this._rateBasedBillingRecordsDataAccess = new ArrayList<RateBasedBillingRecordDataAccess>()
        {{
            add(new RateBasedBillingRecordDataAccess()
                {{
                    setId(1L);
                    setCompanyId(1L);
                    setDescription("description one");
                    setInUse(false);
                    setQuantity(10);
                    setRate(15);
                }}
            );

            add(new RateBasedBillingRecordDataAccess()
                {{
                    setId(2L);
                    setCompanyId(1L);
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

    @AfterEach
    public void resetMocks(){
        Mockito.reset(
                this._flatFeeBillingRecordRepository,
                this._rateBasedBillingRecordRepository
        );
    }

    @Test
    public void testGetAllBillingRecords() throws Exception{

        // registers mock on flat fee & rate base repositories for their find all stubs
        when(this._flatFeeBillingRecordRepository.findAll()).thenReturn(this._flatFeeBillingRecordsDataAccess);
        when(this._rateBasedBillingRecordRepository.findAll()).thenReturn(this._rateBasedBillingRecordsDataAccess);

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

}
