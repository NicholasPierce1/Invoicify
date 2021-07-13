package com.galvanize.invoicify.repository.repositories.flatfeebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FlatFeeBillingRecordRepository extends JpaRepository<FlatFeeBillingRecordDataAccess, Long> {
}
