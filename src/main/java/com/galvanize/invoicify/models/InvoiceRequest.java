package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRequest {

    private String invoiceDescription;
    private List<Long> recordIds = new ArrayList<Long>();





    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }


    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }
}
