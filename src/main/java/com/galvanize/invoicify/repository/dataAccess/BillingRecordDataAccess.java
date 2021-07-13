package com.galvanize.invoicify.repository.dataAccess;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.repository.dataAccess.definition.IDataAccess;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.function.Supplier;

@Entity
abstract class BillingRecordDataAccess implements IDataAccess<BillingRecord> {

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // constructor/s


    // get & set


    // method/s

}
