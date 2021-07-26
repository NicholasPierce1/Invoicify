package com.galvanize.invoicify.repository.repositories.flatfeebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h2>
 *     FlatFeeBillingRecordRepository
 * </h2>
 * <p>
 *     Accessible Bean injection to expose CRUD functionality for FlatFeeBillingRecordDataAccess.
 *     Targeted to be consumed exclusively within the Adapter layer.
 * </p>
 */
@Repository
public interface FlatFeeBillingRecordRepository extends JpaRepository<FlatFeeBillingRecordDataAccess, Long> {
}
