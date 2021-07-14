package com.galvanize.invoicify.repository.repositories.company;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}
