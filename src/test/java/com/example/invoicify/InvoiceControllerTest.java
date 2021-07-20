package com.example.invoicify;

import com.galvanize.invoicify.InvoicifyApplication;
import com.galvanize.invoicify.controllers.InvoiceController;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.repositories.invoicerepository.InvoiceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = InvoicifyApplication.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceControllerTest {

    private InvoiceRepository invoiceRepository;
    private Adapter adapter;
    private InvoiceController invoiceController;

    @BeforeAll
    public void createAdapter() {
        this.invoiceRepository = Mockito.mock(InvoiceRepository.class);
        adapter = new Adapter(invoiceRepository);
        this.invoiceController = new InvoiceController(adapter);
    }

    @Test
    public void createInvoice() throws Exception {

    }





}