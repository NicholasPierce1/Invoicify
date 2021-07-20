package com.galvanize.invoicify.models;

import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private String invoiceDescription;
    private List<String> recordIds = new ArrayList<String>();

    public List<String> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }




}
