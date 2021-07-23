package com.galvanize.invoicify.repository.repositories.companyrepository;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.sun.istack.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * <h2>
 *     This interface extends the JPARepository, which takes in a CompanyDataAccess Object, along
 *      with its serialized Long id. This repository defines a method that finds a company by
 *      its name. Important to note that it inherits the methods from the JPARepository,
 *      so this interface is not confined to just the method that is present. Most of
 *      the methods called on by the CompanyRepository actually are inherited from
 *      parent interface
 * </h2>
 */
@Repository
public interface CompanyRepository extends JpaRepository<CompanyDataAccess, Long> {

    public Optional<CompanyDataAccess> findByName(@NotNull final String name);


}
