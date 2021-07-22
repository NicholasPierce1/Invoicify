package com.example.invoicify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.InvoiceController;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
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

import java.time.LocalDate;
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

    @Autowired
    private FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    @Autowired
    private RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private PasswordEncoder _passwordEncoder;

    private InvoiceRepository _invoiceRepository;
    private InvoiceLineItemRepository _invoiceLineItemRepository;

    private Adapter adapter;
    private InvoiceController invoiceController;

    //@Autowired
    private Authentication auth;

    @BeforeAll
    public void createAdapter() {
        this._invoiceRepository = Mockito.mock(InvoiceRepository.class);
        adapter = new Adapter(
                _userRepository,
                companyRepository,
                _flatFeeBillingRecordRepository,
                _rateBasedBillingRecordRepository,
                _invoiceRepository,
                _invoiceLineItemRepository,
                _passwordEncoder);
        this.invoiceController = new InvoiceController(adapter);
    }

    @Test
    //@WithMockUser(value = "bob")
    public void createInvoice() throws Exception {
        long companyId = 1L;
        long userId = 1L;
        final LocalDate now = LocalDate.now();
        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess(companyId, now, userId, "invoice_test_description");
        Invoice expectedInvoice = new Invoice();

        UserDataAccess userDataAccess = new UserDataAccess("bob", "password");

        when(_userRepository.findByUsername(any())).thenReturn(Optional.of(userDataAccess));
        when(_invoiceRepository.save(any())).thenReturn(invoiceDataAccess);

        Invoice actualInvoice = this.invoiceController.createInvoice(auth, expectedInvoice, 1);
        assertEquals(objectMapper.writeValueAsString(actualInvoice), objectMapper.writeValueAsString(expectedInvoice));
        verify(_invoiceRepository, times(1)).save(any());
    }



}