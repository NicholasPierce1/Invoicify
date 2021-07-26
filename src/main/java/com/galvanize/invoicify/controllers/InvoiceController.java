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

    /**
     *This invoice endpoint creates an invoice.
     *  Process Steps :
     *   1. Check if there is an actual user logged in.
     *   2. Check if IDs (Company ID, and Billing record IDs) in the parameters are valid and existing.
     *   3. Save data to Invoice and InvoiceLineItem entity tables.
     *   4. Create Invoice response object. see@Return
     *
     *
     * @param auth
     * @param invoice
     * {
     *    "invoiceDescription":"new invoice",
     *    "recordIds":[1,2]
     * }
     * @param companyId
     * @return an invoice object that looks something like this
     * @see <a href="https://documenter.getpostman.com/view/11036917/TzedhkB1#10cb449b-7938-4772-8368-667999b6f86b"> Create Invoice Response</a>
     *
     */
    @PostMapping("/{companyId}")
    public Invoice createInvoice(Authentication auth, @RequestBody Invoice invoice, @PathVariable long companyId) {
        String userName = "";
        if (auth != null) {
             User user = (User) auth.getPrincipal();
             userName = user.getUsername();
        }
        try {
            return adapter.createInvoice(invoice, companyId, userName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     *
     * @return a list of all Invoice objects.
     * @see <a href="https://documenter.getpostman.com/view/11036917/TzedhkB1#10cb449b-7938-4772-8368-667999b6f86b"> Create Invoice Response</a>
     */
    @GetMapping()
    public List<Invoice> getAllInvoices() {
        return adapter.getInvoices();
    }

}
