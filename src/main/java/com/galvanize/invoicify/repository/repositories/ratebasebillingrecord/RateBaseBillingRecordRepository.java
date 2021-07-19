package com.galvanize.invoicify.repository.repositories.ratebasebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateBaseBillingRecordRepository extends JpaRepository<RateBasedBillingRecordDataAccess, Long> {
}
