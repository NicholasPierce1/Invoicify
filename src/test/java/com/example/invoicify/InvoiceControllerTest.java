package com.example.invoicify;

import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.InvoiceController;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceControllerTest {

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
                _passwordEncoder);
        this.invoiceController = new InvoiceController(adapter);
    }

    @Test
    public void createInvoice() throws Exception {
        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess();
        Invoice invoice = new Invoice();
        when(_invoiceRepository.save(any())).thenReturn(invoiceDataAccess);
        this.invoiceController.createInvoice(auth, invoice, 1);
        verify(_invoiceRepository, times(1)).save(any());
    }



}