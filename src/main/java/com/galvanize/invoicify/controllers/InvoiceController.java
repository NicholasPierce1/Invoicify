package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * <h2>
 *     The Invoice Controller Class is responsible for facilitating business logic to the adapter to fulfill invoice endpoint responses. It takes in requests
 *     by request, and based off of the endpoint, it will either return to the user a list of Invoices, or an individual
 *     invoice. This controller is expected to perform a custom create, and custom read crud method (implemented using a sql connection stream that executes a custom SQL query).
 * </h2>
 */
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    /**
     * <p>
     *     In order to connect a request to its corresponding response, this Adapter type adapter lays the bridge
     *     from modelObject to mDataAccessObject.
     * </p>
     */
    private Adapter adapter;

    /**
     * <p>
     *      Autowired constructor that takes in the Adapter bean and renders a bean of type InvoiceController.
     * </p>
     * @param adapter -> preexisting bean injection servicing the remote data store
     */
    @Autowired
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
            System.out.println(e.getMessage());
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
