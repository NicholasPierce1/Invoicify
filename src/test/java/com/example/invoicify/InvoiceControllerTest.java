package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.InvoiceController;
import com.galvanize.invoicify.models.Invoice;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

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

    @BeforeAll
    public void createAdapter() {
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
    }

    String expectedCreateInvoiceResponseStr = "{" +
            "  \"id\": 1," +
            "  \"company\": {" +
            "    \"id\": 1," +
            "    \"name\": \"Vapianos Ltd.\"" +
            "  }," +
            "  \"createdOn\": \"2015-05-07\"," +
            "  \"createdBy\": {" +
            "    \"id\": 1," +
            "    \"password\": \"$2a$10$d1wOiO11zyY.iqptZNiMnezV4FnMvN5TPRTTMC5V89IhnXqhCkdzm\"," +
            "    \"username\": \"admin\"," +
            "    \"enabled\": true," +
            "    \"authorities\": null," +
            "    \"accountNonExpired\": true," +
            "    \"accountNonLocked\": true," +
            "    \"credentialsNonExpired\": true" +
            "  }," +
            "  \"invoiceDescription\": \"new invoice\"," +
            "  \"lineItems\": [" +
            "    {" +
            "      \"id\": 1," +
            "      \"createdOn\": \"2015-05-07\"," +
            "      \"createdBy\": {" +
            "        \"id\": 1," +
            "        \"password\": \"$2a$10$d1wOiO11zyY.iqptZNiMnezV4FnMvN5TPRTTMC5V89IhnXqhCkdzm\"," +
            "        \"username\": \"admin\"," +
            "        \"enabled\": true," +
            "        \"authorities\": null," +
            "        \"accountNonExpired\": true," +
            "        \"accountNonLocked\": true," +
            "        \"credentialsNonExpired\": true" +
            "      }" +
            "    }," +
            "    {" +
            "      \"id\": 2," +
            "      \"createdOn\": \"2015-05-07\"," +
            "      \"createdBy\": {" +
            "        \"id\": 1," +
            "        \"password\": \"$2a$10$d1wOiO11zyY.iqptZNiMnezV4FnMvN5TPRTTMC5V89IhnXqhCkdzm\"," +
            "        \"username\": \"admin\"," +
            "        \"enabled\": true," +
            "        \"authorities\": null," +
            "        \"accountNonExpired\": true," +
            "        \"accountNonLocked\": true," +
            "        \"credentialsNonExpired\": true" +
            "      }" +
            "    }" +
            "  ]" +
            "}";

    String expectedFetchInvoicesResponseStr = "[{" +
            "  \"id\": 1," +
            "  \"company\": {" +
            "    \"id\": 1," +
            "    \"name\": \"Vapianos Ltd.\"" +
            "  }," +
            "  \"createdOn\": \"2015-05-07\"," +
            "  \"createdBy\": {" +
            "    \"id\": 1," +
            "    \"password\": \"$2a$10$d1wOiO11zyY.iqptZNiMnezV4FnMvN5TPRTTMC5V89IhnXqhCkdzm\"," +
            "    \"username\": \"admin\"," +
            "    \"enabled\": true," +
            "    \"authorities\": null," +
            "    \"accountNonExpired\": true," +
            "    \"accountNonLocked\": true," +
            "    \"credentialsNonExpired\": true" +
            "  }," +
            "  \"invoiceDescription\": \"new invoice\"," +
            "  \"lineItems\": [" +
            "    {" +
            "      \"id\": 1," +
            "      \"createdOn\": \"2015-05-07\"," +
            "      \"createdBy\": {" +
            "        \"id\": 1," +
            "        \"password\": \"$2a$10$d1wOiO11zyY.iqptZNiMnezV4FnMvN5TPRTTMC5V89IhnXqhCkdzm\"," +
            "        \"username\": \"admin\"," +
            "        \"enabled\": true," +
            "        \"authorities\": null," +
            "        \"accountNonExpired\": true," +
            "        \"accountNonLocked\": true," +
            "        \"credentialsNonExpired\": true" +
            "      }" +
            "    }," +
            "    {" +
            "      \"id\": 2," +
            "      \"createdOn\": \"2015-05-07\"," +
            "      \"createdBy\": {" +
            "        \"id\": 1," +
            "        \"password\": \"$2a$10$d1wOiO11zyY.iqptZNiMnezV4FnMvN5TPRTTMC5V89IhnXqhCkdzm\"," +
            "        \"username\": \"admin\"," +
            "        \"enabled\": true," +
            "        \"authorities\": null," +
            "        \"accountNonExpired\": true," +
            "        \"accountNonLocked\": true," +
            "        \"credentialsNonExpired\": true" +
            "      }" +
            "    }" +
            "  ]" +
            "}]";



    @Test
    //@WithMockUser(value = "bob")
    public void createInvoice() throws Exception {
        long companyId = 1L;
        long userId = 1L;

        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess(companyId, new Date(), userId, "invoice_test_description");
        InvoiceLineItemDataAccess invoiceLineItemDataAccess = new InvoiceLineItemDataAccess();
        CompanyDataAccess company = new CompanyDataAccess();


        Invoice invoiceRequest = new Invoice();
        List<Long> recordIds = new ArrayList<Long>();
        recordIds.add(1L);
        recordIds.add(2L);

        invoiceRequest.setInvoiceDescription("Invoice Description");
        invoiceRequest.setRecordIds(recordIds);

        Invoice expectedInvoice = new Invoice();
        UserDataAccess userDataAccess = new UserDataAccess("bob", "password");
        userDataAccess.setId(userId);


        when(_companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(_flatFeeBillingRecordRepository.existsById(any())).thenReturn(true);
        when(_userRepository.findByUsername(any())).thenReturn(Optional.of(userDataAccess));
        when(_invoiceRepository.save(any())).thenReturn(invoiceDataAccess);
        when(_invoiceLineItemRepository.save(any())).thenReturn(invoiceLineItemDataAccess);
        when(_invoiceRepository.fetchInvoice(1L, recordIds)).thenReturn(invoiceDataAccess);

        Invoice actualInvoice = this.invoiceController.createInvoice(auth, invoiceRequest, 1);
        assertEquals(objectMapper.writeValueAsString(actualInvoice), expectedCreateInvoiceResponseStr);


        //verify(_invoiceRepository, times(1)).save(any());


    }

    @Test
    public void getInvoices() throws Exception {
        long companyId = 1L;
        long userId = 1L;
        Invoice expectedInvoice = new Invoice();

        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess(companyId, new Date(), userId, "invoice_test_description");
        List<InvoiceDataAccess> listOfDataAccesses = new ArrayList<InvoiceDataAccess>();
        listOfDataAccesses.add(invoiceDataAccess);

        when(_invoiceRepository.fetchInvoices(0L, null)).thenReturn(listOfDataAccesses);

        List<Invoice> actualInvoices = this.invoiceController.getAllInvoices();
        assertEquals(objectMapper.writeValueAsString(actualInvoices), expectedFetchInvoicesResponseStr);
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


}