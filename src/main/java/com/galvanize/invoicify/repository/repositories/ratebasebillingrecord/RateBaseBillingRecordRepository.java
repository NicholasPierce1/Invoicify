package com.galvanize.invoicify.repository.repositories.ratebasebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <h2>
 *     RateBaseBillingRecordRepository
 * </h2>
 * <p>
 *     Accessible Bean injection to expose CRUD functionality for RateBasedBillingRecordDataAccess.
 *     Targeted to be consumed exclusively within the Adapter layer.
 * </p>
 */
@Repository
public interface RateBaseBillingRecordRepository extends JpaRepository<RateBasedBillingRecordDataAccess, Long> {
}
