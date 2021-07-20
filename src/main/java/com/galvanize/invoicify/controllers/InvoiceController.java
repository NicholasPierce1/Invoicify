package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.InvoiceRequest;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public Invoice createInvoice(Authentication auth, @RequestBody InvoiceRequest invoiceRequest, @PathVariable long companyId) {
        String userName = "";
        if (auth != null) {
             User user = (User) auth.getPrincipal();
             userName = user.getUsername();
            System.out.println("here" + auth.getPrincipal());
        }
        return adapter.createInvoice(invoiceRequest, companyId, userName);
    }

}
