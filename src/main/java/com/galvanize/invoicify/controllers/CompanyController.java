package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/company")
public class CompanyController  {

//    @Autowired
    private final Adapter adapter;

    @Autowired
    public CompanyController(Adapter adapter){
        this.adapter = adapter;
    }

    @GetMapping("/all")
    public List<Company> viewAllCompanies() {
        System.out.println("Before");
        return adapter.findAllCompaniesBasic();

    }

    @GetMapping(
            value = {"/{id}"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Company findById(@PathVariable Long id) {

        return adapter.findCompanyById(id);

    }

    @PostMapping
    public Company addCompany(@RequestBody Company company) {
        return adapter.createCompany(company);
    }

    @DeleteMapping(
            value = {"/{id}"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Company deleteCompanyById(@PathVariable Long id) {
         return adapter.deleteCompany(id);
    }
}
