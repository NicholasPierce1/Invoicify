package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.repositories.company.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class Adapter {

    private final UserRepository _userRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public Adapter(UserRepository userRepository, CompanyRepository companyRepository){
        this._userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    // ...stubs go below
    // add your method signatures to complete your user stories here

    public List<Company> findAllCompaniesBasic(){
        return this.companyRepository.findAll(); // todo: implement
    };

}
