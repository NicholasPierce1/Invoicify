package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.FlatFeeBillingRecord;

import javax.persistence.*;
import java.util.function.Supplier;

/**
 * <h2>
 *     FlatFeeBillingRecordDataAccess
 * </h2>
 * <p>
 *     Serves as the data definition for a FlatFeeBillingRecordDataAccess for the remote data store endpoint.
 *     Functionality exists to encapsulate DA to/from Model conversion
 *     Note: Not exhaustive for this entity definition. Maps with referential BillingRecordDataAccess's definition
 *     for FlatFeeBillingRecordDataAccess complete definition.
 * </p>
 */
@Entity(name = "FLAT_FEE_BILLING_RECORD")
public class FlatFeeBillingRecordDataAccess extends BillingRecordDataAccess<FlatFeeBillingRecord> {

    // fields

    /**
     * <p>
     *     represents the total due for this FlatFeeBillingRecordDataAccess
     * </p>
     */
    @Column(nullable = false)
    private double amount;

    // constructor/s
    /**
     * <p>
     *     No arg-constructor to be created for DA->Model conversion and JacksonJSON serialization in custom
     *     ORM.
     * </p>
     */
    public FlatFeeBillingRecordDataAccess(){

    }

    // get & set
    /**
     * <p>
     *     returns amount
     * </p>
     * @return instance value of amount for this FlatFeeBillingRecordDataAccess
     */
    public double getAmount() {
        return amount;
    }

    /**
     * <p>
     *     sets the amount value for this FlatFeeBillingRecordDataAccess
     * </p>
     * @param amount: new amount value for this FlatFeeBillingRecordDataAccess
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // method/s

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    /**
     * <p>
     *     IConvertible's implementation to convert a FlatFeeBillingRecordDataAccess to it's model type parameter
     *     (FlatFeeBillingRecord).
     * </p>
     * @param supplier: supplements a model object that extends (concrete implementation) of FlatFeeBillingRecord
     * @param <M>: extension (concrete implementation) of FlatFeeBillingRecord
     * @return a model object of aforementioned type parameter with the reflective state of 'this'
     * FlatFeeBillingRecordDataAccess
     */
    @Override
    public <M extends FlatFeeBillingRecord> M convertToModel(Supplier<M> supplier) {

        final M flatFeeBillingRecord = super.convertToModel(supplier);
        flatFeeBillingRecord.setAmount(this.getAmount());

        return flatFeeBillingRecord;

    }

    /**
     * <p>
     *     IConvertible's implementation to convert a model to a FlatFeeBillingRecordDataAccess
     * </p>
     * @param modelObject a model object that extends (concrete implementation) of FlatFeeBillingRecord
     * @param <M> extension (concrete implementation) of FlatFeeBillingRecord
     */
    @Override
    public <M extends FlatFeeBillingRecord> void convertToDataAccess(M modelObject) {
        super.convertToDataAccess(modelObject);
        this.setAmount(modelObject.getAmount());
    }


}
