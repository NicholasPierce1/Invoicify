package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.RateBasedBillingRecord;

import javax.persistence.*;
import java.util.function.Supplier;

@Entity(name = "RATE_BASED_BILLING_RECORD")
public class RateBasedBillingRecordDataAccess extends BillingRecordDataAccess<RateBasedBillingRecord> {

    // fields

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Long id;

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
    public <M extends RateBasedBillingRecord> M convertTo(Supplier<M> supplier) {

        final M rateBaseBillingRecord = super.convertTo(supplier);

        rateBaseBillingRecord.setQuantity(this.getQuantity());
        rateBaseBillingRecord.setRate(this.getRate());

        return rateBaseBillingRecord;
    }

    @Override
    public void convertTo(RateBasedBillingRecord modelObject) {
        super.convertTo(modelObject);
        this.setQuantity(modelObject.getQuantity());
        this.setRate(modelObject.getRate());
    }
}
