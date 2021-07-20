package com.galvanize.invoicify.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private String invoiceDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private List<String> recordIds = new ArrayList<String>();

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }


    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }
}
