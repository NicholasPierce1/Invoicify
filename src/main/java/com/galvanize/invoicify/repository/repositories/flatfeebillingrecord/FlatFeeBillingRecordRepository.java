package com.galvanize.invoicify.repository.repositories.flatfeebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * <p>
     *     custom query to get the max id of flat_fee_billing_record table to use when creating billing records (both flat fee and rate based)
     * </p>
     * @return the max id of flat_fee_billing_record
     */
    @Query(value = "SELECT max(id) from FLAT_FEE_BILLING_RECORD", nativeQuery = true)
    public long getMaxId();

}
