package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import java.util.List;

/**
 *<p>
 *     this interface is being implemented by the invoice manager(InvoiceRepositoryImpl.java) and InvoiceRepository to connect both and use custom queries.
 *</p>
 */
public interface InvoiceRepositoryCustom {

    /**
     * fetchInvoice interface method for fetching a single invoice after creation
     * @param invoiceId - this is passed in from the client.
     * @param recordIds - list of record ids that were passed in from the client.
     * @return InvoiceDataAccess
     */
    InvoiceDataAccess fetchInvoice(long invoiceId, List<Long> recordIds);

    /**
     * <p>
     *     fetch all invoices don't require params so the parameters don't contain any valuable data.
     * </p>
     * @param invoiceId - this is usually 0
     * @param recordIds - this is usually empty
     * @return list of all invoices.
     */
    List<InvoiceDataAccess> fetchInvoices(long invoiceId, List<Long> recordIds);

}
