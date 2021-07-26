package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.configuration.InvoicifyConfiguration;
import com.galvanize.invoicify.controllers.InvoiceController;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceLineItemDataAccess;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.invoicelineitemrepository.InvoiceLineItemRepository;
import com.galvanize.invoicify.repository.repositories.invoicerepository.InvoiceRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {InvoicifyApplication.class, InvoicifyConfiguration.class})
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    private FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    private RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    private CompanyRepository _companyRepository;

    private UserRepository _userRepository;

    @Autowired
    private PasswordEncoder _passwordEncoder;

    private InvoiceRepository _invoiceRepository;
    private InvoiceLineItemRepository _invoiceLineItemRepository;

    private Adapter adapter;
    private InvoiceController invoiceController;

    private Authentication auth;

    private long companyId;
    private long userId;
    private long invoiceId;
    private String username;
    private String password;
    private String invoiceDescription;
    private String createdOnStr;
    private Date createdOn;
    private CompanyDataAccess companyDataAccess;
    private UserDataAccess userDataAccess;
    private ArrayList<InvoiceLineItemDataAccess> lineItems;
    private InvoiceDataAccess invoiceDataAccess;
    private InvoiceLineItemDataAccess invoiceLineItemDataAccess;
    private InvoiceLineItemDataAccess invoiceLineItemDataAccess2;


    @BeforeAll
    public void createAdapter() throws Exception {
        this._invoiceRepository = Mockito.mock(InvoiceRepository.class);
        this._userRepository = Mockito.mock(UserRepository.class);
        this._companyRepository = Mockito.mock(CompanyRepository.class);
        this._flatFeeBillingRecordRepository = Mockito.mock(FlatFeeBillingRecordRepository.class);
        this._invoiceLineItemRepository = Mockito.mock(InvoiceLineItemRepository.class);
        adapter = new Adapter(
                _userRepository,
                _companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _invoiceRepository,
                _invoiceLineItemRepository,
                _passwordEncoder);
        this.invoiceController = new InvoiceController(adapter);
        //the get and create endpoints are using the same object so just initialize the objects inside here.
        initializeObjects();
    }


    private void initializeObjects() throws Exception {
        companyId = 1L;
        userId = 1L;
        invoiceId = 1L;

        username = "admin";
        password = "$2a$10$uQ/Y566Nkp67exYhhoTd8e27uFEF7imX7Q3QhF.0HtLJmXQYUJJ4m";
        invoiceDescription = "test desc";
        createdOn = new Date();

        //initialize data accesses models
        companyDataAccess = new CompanyDataAccess(companyId, "Subway");
        userDataAccess = new UserDataAccess(username, password);
        userDataAccess.setId(userId);

        invoiceDataAccess = new InvoiceDataAccess(companyId, createdOn, userId, invoiceDescription);
        invoiceDataAccess.setId(invoiceId);
        invoiceDataAccess.setCompany(companyDataAccess);
        invoiceDataAccess.setCreatedOn(createdOn);
        invoiceDataAccess.setUser(userDataAccess);
        invoiceDataAccess.setDescription(invoiceDescription);

        lineItems = new ArrayList<InvoiceLineItemDataAccess>();

        //1st invoice line item
        invoiceLineItemDataAccess = new InvoiceLineItemDataAccess();
        invoiceLineItemDataAccess.setId(4);
        invoiceLineItemDataAccess.setCreatedOn(createdOn);
        invoiceLineItemDataAccess.setInvoiceId(invoiceId);
        invoiceLineItemDataAccess.setUser(userDataAccess);
        lineItems.add(invoiceLineItemDataAccess);


        //2nd invoice line item
        invoiceLineItemDataAccess2 = new InvoiceLineItemDataAccess();
        invoiceLineItemDataAccess2.setId(5);
        invoiceLineItemDataAccess2.setCreatedOn(createdOn);
        invoiceLineItemDataAccess2.setInvoiceId(invoiceId);
        invoiceLineItemDataAccess2.setUser(userDataAccess);
        lineItems.add(invoiceLineItemDataAccess2);

        invoiceDataAccess.setLineItems(lineItems);
    }


    @Test
    public void createInvoice() throws Exception {
        //initialize request object. this is the exact data that are passed in when creating an invoice.
        Invoice invoiceRequest = new Invoice();
        List<Long> recordIds = new ArrayList<Long>();
        recordIds.add(1L);
        recordIds.add(2L);
        invoiceRequest.setInvoiceDescription("test desc");
        invoiceRequest.setRecordIds(recordIds);

        //initialize the mock Create invoice response.
        InvoiceDataAccess invoiceDataAccessResp = new InvoiceDataAccess();
        invoiceDataAccessResp.setId(invoiceId);
        invoiceDataAccessResp.setCompany(companyDataAccess);
        invoiceDataAccessResp.setCreatedOn(createdOn);
        invoiceDataAccessResp.setUser(userDataAccess);
        invoiceDataAccessResp.setDescription(invoiceDescription);
        invoiceDataAccessResp.setLineItems(lineItems);

        Invoice expectedInvoice = invoiceDataAccessResp.convertToModel(Invoice::new);

        //to pass create invoice validation
        when(_companyRepository.findById(companyId)).thenReturn(Optional.of(companyDataAccess));
        //to pass create invoice validation
        when(_flatFeeBillingRecordRepository.existsById(any())).thenReturn(true);
        when(_userRepository.findByUsername(any())).thenReturn(Optional.of(userDataAccess));
        when(_invoiceRepository.save(any())).thenReturn(invoiceDataAccess);  //invoiceDataAccess is initialize in @BeforeAll.
        when(_invoiceLineItemRepository.save(any())).thenReturn(invoiceLineItemDataAccess2);
        when(_invoiceRepository.fetchInvoice(anyLong(), anyList())).thenReturn(invoiceDataAccess); //invoiceDataAccess is initialize in @BeforeAll.


        Invoice actualInvoice = this.invoiceController.createInvoice(auth, invoiceRequest, companyId);
        assertEquals(objectMapper.writeValueAsString(expectedInvoice),objectMapper.writeValueAsString(actualInvoice));
    }

    @Test
    public void getInvoices() throws Exception {

        List<InvoiceDataAccess> invoiceDataAccessList = new ArrayList<InvoiceDataAccess>();
        //invoiceDataAccess is initialize in @BeforeAll.
        invoiceDataAccessList.add(invoiceDataAccess);
        // creates the expected Invoice that's qualified from the pending mock and conversion operations enclosed
        // within the adapter
        final List<Invoice> expectedInvoiceList = invoiceDataAccessList
                .stream()
                .map((invoiceDataAccess -> invoiceDataAccess.convertToModel(Invoice::new)))
                .collect(Collectors.toList());
        when(_invoiceRepository.fetchInvoices(anyLong(), anyList())).thenReturn(invoiceDataAccessList);
        List<Invoice> actualInvoiceList = this.invoiceController.getAllInvoices();
        assertEquals(objectMapper.writeValueAsString(expectedInvoiceList), objectMapper.writeValueAsString(actualInvoiceList));
    }


    @Test
    public void testCreateInvoiceBillingRecordOrClause() {
        List<Long> recordIds = new ArrayList<Long>();
        recordIds.add(1L);
        recordIds.add(2L);
        recordIds.add(3L);
        String recordIdsStr = " and (";

        for (int i = 0; i < recordIds.size(); i++) {
            final String or = " or ";
            String placeHolder = "t1.id = %d";
            if (i != recordIds.size() - 1 ) {// not last element
                placeHolder = placeHolder.concat(or);
            }
            recordIdsStr = recordIdsStr.concat(String.format(placeHolder, recordIds.get(i)));
        }
        recordIdsStr += ")";

        System.out.println(recordIdsStr);
    }

    // this is a test to make sure that null is returned if one of the ids are invalid.
    @Test
    public void testCreateInvoiceWithInvalidParams() throws Exception {
        Invoice invoiceRequest = new Invoice();
        List<Long> recordIds = new ArrayList<Long>();
        recordIds.add(1L);
        recordIds.add(2L);
        invoiceRequest.setInvoiceDescription("test desc");
        invoiceRequest.setRecordIds(recordIds);

        when(_companyRepository.findById(companyId)).thenReturn(Optional.empty());
        when(_flatFeeBillingRecordRepository.existsById(any())).thenReturn(false);

        Invoice actualInvoice = invoiceController.createInvoice(auth, invoiceRequest, companyId);
        assertNull(actualInvoice);

    }


}