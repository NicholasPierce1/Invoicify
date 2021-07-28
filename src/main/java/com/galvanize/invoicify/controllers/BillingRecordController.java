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

/**
 * <h2>
 *     BillingRecordController
 * </h2>
 * <p>
 *     API Rest controller to facilitate the externally exposed interactions for CRUD operations on
 *     BillingRecords.
 * </p>
 */
@RestController
@RequestMapping(value = {"/api/billing-record/", "/api/billing-record"})
public class BillingRecordController {

    /**
     * <p>
     *     Autowired members to acquire access to encapsulated, remote-data-store CRUD operations
     * </p>
     */
    private final Adapter _adapter;

    /**
     * <p>
     *     Autowired constructor to render a BillingRecordController bean.
     *     Note: not autowiring individual members to be compatible with test mocks.
     * </p>
     * @param adapter: Autowired members to acquire access to encapsulated, remote-data-store CRUD operations
     */
    @Autowired
    public BillingRecordController(Adapter adapter){
        this._adapter = adapter;
    }

    /**
     * <p>
     *   Controller stub for procuring all billing records with children
     *   state attached.
     * </p>
     * @return Optional List of BillingRecords with children state attached
     */
    @RequestMapping(
            value = {"/", ""},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull Optional<List<BillingRecord>> getAllBillingRecords(){
        try {
            return Optional.of(this._adapter.getAllBillingRecords());
        }
        catch(Exception exception){
            System.out.println(exception.getMessage());

            return Optional.empty();
        }
    }

    /**
     * <p>
     *     Retrieves a BillingRecord via its ID with children state attached.
     * </p>
     * @param billingRecordId: required, non-null path variable representing the ID value of a
     *                       preexisting BillingRecord.
     * @return an Optional BillingRecord varying on if the ID maps to a preexisting BillingRecord
     */
    @RequestMapping(
            value = {"/{id}", "/{id}/"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull Optional<BillingRecord> getBillingRecordById(@NotNull final @PathVariable(value = "id") Long billingRecordId){
        try {
            return this._adapter.getBillingRecordById(billingRecordId);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());

            return Optional.empty();
        }
    }

    /**
     * <p>
     *     Saves a FlatFeeBillingRecord with composite state passed via request body.
     *     Composite: non-ID attached.
     * </p>
     * @param flatFeeBillingRecord composite FlatFeeBillingRecord with children ID's attached
     * @return a full FlatFeeBillingRecord with children state qualified and append with
     * ID of parent entity attached.
     * Note: will be null if ID's values of children do NOT map to a preexisting child entity.
     */
    @RequestMapping(
            value = {"/flat-fee", "/flat-fee/"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<FlatFeeBillingRecord> saveFlatFeeBillingRecord(
            @NotNull final @RequestBody FlatFeeBillingRecord flatFeeBillingRecord
    ){
        try {
            return this._adapter.saveFlatFeeBillingRecord(flatFeeBillingRecord);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());

            return Optional.empty();
        }
    }

    /**
     * <p>
     *     Saves a RateBasedBillingRecord with composite state passed via request body.
     *     Composite: non-ID attached.
     * </p>
     * @param rateBasedBillingRecord composite RateBasedBillingRecord with children ID's attached
     * @return a full RateBasedBillingRecord with children state qualified and append with
     * ID of parent entity attached.
     * Note: will be null if ID's values of children do NOT map to a preexisting child entity.
     */
    @RequestMapping(
            value = {"/rate-based", "/rate-based/"},
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<RateBasedBillingRecord> saveRateBasedBillingRecord(
            @NotNull final @RequestBody RateBasedBillingRecord rateBasedBillingRecord
    ){
        try {
            return this._adapter.saveRateBasedFeeBillingRecord(rateBasedBillingRecord);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());

            return Optional.empty();
        }
    }

}
