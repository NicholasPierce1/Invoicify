package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h2>
 *     This interface extends the JPARepository, which takes in a InvoiceDataAccess Object, along
 *      with its serialized Long id. This method implements InvoiceRepositoryCustom to run custom queries and return a cutom invoice response.
 * </h2>
 */
@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceDataAccess, Long>, InvoiceRepositoryCustom {


}
