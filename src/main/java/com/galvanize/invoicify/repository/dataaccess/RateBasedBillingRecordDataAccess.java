package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.RateBasedBillingRecord;

import javax.persistence.*;
import java.util.function.Supplier;

/**
 * <h2>
 *     RateBasedBillingRecordDataAccess
 * </h2>
 * <p>
 *     Serves as the data definition for a RateBasedBillingRecordDataAccess for the remote data store endpoint.
 *     Functionality exists to encapsulate DA to/from Model conversion
 *     Note: Not exhaustive for this entity definition. Maps with referential BillingRecordDataAccess's definition
 *     for RateBasedBillingRecordDataAccess complete definition.
 * </p>
 */
@Entity(name = "RATE_BASED_BILLING_RECORD")
public class RateBasedBillingRecordDataAccess extends BillingRecordDataAccess<RateBasedBillingRecord> {

    // fields

    @Column(nullable = false)
    public double rate;

    @Column(nullable = false)
    public double quantity;

    // constructors

    public RateBasedBillingRecordDataAccess(){

    }

    // get & set

    public double getRate() {
        return rate;
    }

    public double getQuantity() {
        return quantity;
    }

    public Long getId() {
        return id;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // methods

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends RateBasedBillingRecord> M convertToModel(Supplier<M> supplier) {

        final M rateBaseBillingRecord = super.convertToModel(supplier);

        rateBaseBillingRecord.setQuantity(this.getQuantity());
        rateBaseBillingRecord.setRate(this.getRate());

        return rateBaseBillingRecord;
    }

    @Override
    public <M extends RateBasedBillingRecord> void convertToDataAccess(M modelObject) {
        super.convertToDataAccess(modelObject);
        this.setQuantity(modelObject.getQuantity());
        this.setRate(modelObject.getRate());
    }
}
