package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Invoice createInvoice(@RequestBody Invoice invoice, @RequestParam long companyId) {
        return adapter.createInvoice(invoice, companyId);
    }

}
