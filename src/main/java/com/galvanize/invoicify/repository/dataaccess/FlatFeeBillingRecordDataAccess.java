package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.FlatFeeBillingRecord;

import javax.persistence.*;
import java.util.function.Supplier;

@Entity(name = "FLAT_FEE_BILLING_RECORD")
public class FlatFeeBillingRecordDataAccess extends BillingRecordDataAccess<FlatFeeBillingRecord> {

    // fields
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Long id;

    @Column(nullable = false)
    private double amount;

    // constructor/s

    public FlatFeeBillingRecordDataAccess(){

    }

    // get & set

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // method/s

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends FlatFeeBillingRecord> M convertToModel(Supplier<M> supplier) {

        final M flatFeeBillingRecord = super.convertToModel(supplier);
        flatFeeBillingRecord.setAmount(this.getAmount());

        return flatFeeBillingRecord;

    }

    @Override
    public <M extends FlatFeeBillingRecord> void convertToDataAccess(M modelObject) {
        super.convertToDataAccess(modelObject);
        this.setAmount(modelObject.getAmount());
    }


}
