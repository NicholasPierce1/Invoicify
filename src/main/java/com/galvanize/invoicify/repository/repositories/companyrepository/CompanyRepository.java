package com.galvanize.invoicify.repository.repositories.companyrepository;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyDataAccess, Long> {

//    Company findById(Long id);
    public Optional<CompanyDataAccess> findByName(String name);

//    @Query(value = "SELECT count(*) from app_company WHERE company_name = ?1",nativeQuery = true)
//    int countCompaniesByName(String name);

}
