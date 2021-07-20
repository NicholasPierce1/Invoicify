package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice/")
public class InvoiceController {
    private Adapter adapter;

    public InvoiceController(Adapter adapter) {
        this.adapter = adapter;
    }

    @PostMapping
    public void createInvoice(@RequestBody Invoice invoice, @RequestParam long companyId) {

    }

}
