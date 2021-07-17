package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public final class Adapter {

    private final UserRepository _userRepository;
    private final CompanyRepository _companyRepository;

    @Autowired
    public Adapter(UserRepository userRepository, CompanyRepository companyRepository){
        this._userRepository = userRepository;
        this._companyRepository = companyRepository;
    }

    // ...stubs go below
    // add your method signatures to complete your user stories here

    public List<Company> findAllCompaniesBasic(){
        return this._companyRepository
                .findAll()
                .stream()
                .map( (companyDataAccess) -> companyDataAccess.convertTo(Company::new) )
                .collect(Collectors.toList());
    }

    public Company findCompanyById(@PathVariable long id) {

        return this._companyRepository
                .findById(id)
                .map(companyDataAccess -> companyDataAccess.convertTo(Company::new)).get();
    }
}
