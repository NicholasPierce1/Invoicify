package com.galvanize.invoicify.repository.repositories.flatfeebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Component
public class FlatFeeBillingRecordManagerImpl implements FlatFeeBillingRecordManagerRepository{

    private final EntityManagerFactory _entityManagerFactory;

    private final EntityManager _entityManager;

    @Autowired
    public FlatFeeBillingRecordManagerImpl(EntityManagerFactory entityManager){
        this._entityManagerFactory = entityManager;

        this._entityManager = this._entityManagerFactory.createEntityManager();
    }

    @Override
    public FlatFeeBillingRecordDataAccess getFlatFeeById(Long ID) {
        return null;
    }
}
