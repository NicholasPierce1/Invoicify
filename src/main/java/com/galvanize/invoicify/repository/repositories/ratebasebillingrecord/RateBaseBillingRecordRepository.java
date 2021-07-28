package com.galvanize.invoicify.repository.repositories.ratebasebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * <p>
     *     custom query to get the max id of rate_based_billing_record table to use when creating billing records (both flat fee and rate based)
     * </p>
     * @return the max id of rate_based_billing_Record
     */
    @Query(value = "SELECT max(id) from RATE_BASED_BILLING_RECORD", nativeQuery = true)
    public Long getMaxId();
}
