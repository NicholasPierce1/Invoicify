package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.function.Supplier;

@Entity
@Table(name = "invoice")
public class InvoiceDataAccess implements IDataAccess<Invoice> {



    @Override
    public <M extends Invoice> M convertTo(Supplier<M> supplier) {
        final M invoice = supplier.get();
        return invoice;
    }

    @Override
    public void createDataAccess(Object[] dbo) {

    }


}
