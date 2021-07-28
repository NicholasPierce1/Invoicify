package com.galvanize.invoicify.repository.repositories.invoicelineitemrepository;

import com.galvanize.invoicify.repository.dataaccess.InvoiceLineItemDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h2>
 *     InvoiceLineItemRepository
 * </h2>
 * <p>
 *     This interface extends the JPARepository, which takes in a InvoiceLineItemDataAccess Object, along
 *     with its serialized Long id. This repository is only used for saving invoice line item records to InvoiceLineItem entity table
 * </p>
 */
@Repository
public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItemDataAccess, Long> {

}
