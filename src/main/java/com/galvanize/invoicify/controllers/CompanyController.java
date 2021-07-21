package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.adapter.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * <h2>
 *     todo: describe here
 * </h2>
 */
@RestController
@RequestMapping("/api/company")
public class CompanyController  {

    /**
     * <p>
     *     todo: describe here
     * </p>
     */
    private final Adapter adapter;

    /**
     * <p>
     *  todo: describe here
     * </p>
     * @param adapter
     */
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
    public Optional<Company> deleteCompanyById(@PathVariable Long id) {
         return adapter.deleteCompany(id);
    }

    @PutMapping(
            value = {"/{id}"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Company updateCompany(@RequestBody Company company, @PathVariable Long id) throws Exception {
        return adapter.updateCompany(company, id);
    }
}
