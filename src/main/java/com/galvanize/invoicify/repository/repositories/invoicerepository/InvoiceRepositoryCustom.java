package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface InvoiceRepositoryCustom {

    InvoiceDataAccess fetchInvoice(long invoiceId, List<Long> recordIds);
    List<InvoiceDataAccess> fetchInvoices(long invoiceId, List<Long> recordIds);

}
