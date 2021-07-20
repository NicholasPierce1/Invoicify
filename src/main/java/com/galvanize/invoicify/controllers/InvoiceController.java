package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;

@RestController
@RequestMapping("/api/invoice/")
public class InvoiceController {
    private Adapter adapter;

    public InvoiceController(Adapter adapter) {
        this.adapter = adapter;
    }

    /*{
        "invoiceDescription":"new invoice",
            "recordIds":[1,2]
    }*/
    @PostMapping
    public Invoice createInvoice(Authentication auth, @RequestBody Invoice invoice, @RequestParam long companyId) {
        User user = (User) auth.getPrincipal();
        return adapter.createInvoice(invoice, companyId, user);
    }

}
