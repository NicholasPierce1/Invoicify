package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.BillingRecord;

import javax.persistence.*;
import java.util.function.Supplier;

@Entity
public class FlatFeeBillingRecordDataAccess extends BillingRecordDataAccess{

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    private Long BillingRecordId;

    private String dummy;

    // constructor/s


    // get & set


    // method/s

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends BillingRecord> M convertTo(Supplier<M> supplier) {
        return null;
    }


}
