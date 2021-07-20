package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceDataAccess, Long> {


}
