package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.FlatFeeBillingRecord;
import com.galvanize.invoicify.models.RateBasedBillingRecord;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/api/billingRecord/", "/api/billingRecord"})
public class BillingRecordController {

    private final Adapter _adapter;

    @Autowired
    public BillingRecordController(Adapter adapter){
        this._adapter = adapter;
    }

    @RequestMapping(
            value = {"/", ""},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull List<BillingRecord> getAllBillingRecords(){
        return this._adapter.getAllBillingRecords();
    }

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull Optional<BillingRecord> getBillingRecordById(@NotNull final @PathVariable(value = "id") Long billingRecordId){
        return this._adapter.getBillingRecordById(billingRecordId);
    }

    @RequestMapping(
            value = {"/flat-fee", "/flat-fee/"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<FlatFeeBillingRecord> saveFlatFeeBillingRecord(
            @NotNull final @RequestBody FlatFeeBillingRecord flatFeeBillingRecord
    ){
        return this._adapter.saveFlatFeeBillingRecord(flatFeeBillingRecord);
    }

    @RequestMapping(
            value = {"/rate-based", "/rate-based/"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<RateBasedBillingRecord> saveRateBasedBillingRecord(
            @NotNull final @RequestBody RateBasedBillingRecord rateBasedBillingRecord
    ){
        return this._adapter.saveRateBasedFeeBillingRecord(rateBasedBillingRecord);
    }

}
