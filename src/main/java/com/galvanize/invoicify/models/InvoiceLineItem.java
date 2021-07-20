package com.galvanize.invoicify.models;

import java.util.Date;

public class InvoiceLineItem {
    private long id;
    private BillingRecord billingRecord;
    private Date dateCreatedOn;
    private User createdBy;
    private InvoiceRequest invoice;
}
