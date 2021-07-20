package com.galvanize.invoicify.repository.repositories.flatfeebillingrecord;

import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordManagerRepository;

@Component
public class FlatFeeBillingRecordManagerImpl implements FlatFeeBillingRecordManagerRepository {

    private final EntityManager _entityManager;

    @Autowired
    public FlatFeeBillingRecordManagerImpl(EntityManagerFactory entityManager){
        final EntityManagerFactory entityManagerFactory = entityManager;

        this._entityManager = entityManagerFactory.createEntityManager();
    }

}
