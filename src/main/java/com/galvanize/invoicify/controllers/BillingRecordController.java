package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billingRecord/")
public class BillingRecordController {

    private final Adapter _adapter;

    @Autowired
    public BillingRecordController(Adapter adapter){
        this._adapter = adapter;
    }

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<BillingRecord> getAllBillingRecords(){
        return this._adapter.getAllBillingRecords();
    }

}
