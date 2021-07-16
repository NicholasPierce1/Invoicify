package com.galvanize.invoicify.repository.repositories.companyrepository;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyDataAccess, Long> {

}
