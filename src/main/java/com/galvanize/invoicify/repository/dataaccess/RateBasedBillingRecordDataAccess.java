package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(value = {"amount"})
public class RateBasedBillingRecordDataAccess extends BillingRecordDataAccess<RateBasedBillingRecord> {

    // fields
    /**
     * <p>
     *     represents the rate for each composite item within the quantity
     *     for this RateBasedBillingRecordDataAccess
     * </p>
     */
    @Column(nullable = false)
    public double rate;

    /**
     * <p>
     *     represents the quantity of each item
     *     for this RateBasedBillingRecordDataAccess
     * </p>
     */
    @Column(nullable = false)
    public double quantity;

    // constructors
    /**
     * <p>
     *     No arg-constructor to be created for DA Model conversion and JacksonJSON serialization in custom
     *     ORM.
     * </p>
     */
    public RateBasedBillingRecordDataAccess(){

    }

    // get & set
    /**
     * <p>
     *     returns rate
     * </p>
     * @return instance value of rate for this RateBasedBillingRecordDataAccess
     */
    public double getRate() {
        return rate;
    }

    /**
     * <p>
     *     returns quantity
     * </p>
     * @return instance value of quantity for this RateBasedBillingRecordDataAccess
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * <p>
     *     sets the rate value for this RateBasedBillingRecordDataAccess
     * </p>
     * @param rate: new rate value for this RateBasedBillingRecordDataAccess
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * <p>
     *     sets the rate quantity for this RateBasedBillingRecordDataAccess
     * </p>
     * @param quantity: new quantity value for this RateBasedBillingRecordDataAccess
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // methods

    @Override
    public <M extends RateBasedBillingRecord> M convertToModel(Supplier<M> supplier) {

        final M rateBaseBillingRecord = super.convertToModel(supplier);

        rateBaseBillingRecord.setQuantity(this.getQuantity());
        rateBaseBillingRecord.setRate(this.getRate());

        return rateBaseBillingRecord;
    }

    /**
     * <p>
     *     IConvertible's implementation to convert a model to a RateBasedBillingRecordDataAccess
     * </p>
     * @param modelObject a model object that extends (concrete implementation) of RateBasedBillingRecord
     * @param <M> extension (concrete implementation) of RateBasedBillingRecord
     */
    @Override
    public <M extends RateBasedBillingRecord> void convertToDataAccess(M modelObject) {
        super.convertToDataAccess(modelObject);
        this.setQuantity(modelObject.getQuantity());
        this.setRate(modelObject.getRate());
    }
}
