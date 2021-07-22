package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    private Adapter adapter;

    public InvoiceController(Adapter adapter) {
        this.adapter = adapter;
    }

    /*{
        "invoiceDescription":"new invoice",
            "recordIds":[1,2]
    }*/
    @PostMapping("/{companyId}")
    public Invoice createInvoice(Authentication auth, @RequestBody Invoice invoice, @PathVariable long companyId) {
        String userName = "";

        if (auth != null) {
             User user = (User) auth.getPrincipal();
             userName = user.getUsername();
        }
        return adapter.createInvoice(invoice, companyId, userName);
    }

    @GetMapping()
    public List<Invoice> getAllInvoices() {
        return adapter.getInvoices();
    }

}
