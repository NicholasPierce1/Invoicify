package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.repository.dataaccess.InvoiceLineItemDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItemDataAccess, Long> {

}
