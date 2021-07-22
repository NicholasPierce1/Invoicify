package com.galvanize.invoicify.controllers;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.adapter.Adapter;
import com.galvanize.invoicify.repository.adapter.DuplicateCompanyException;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
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
    public @NotNull Optional<List<Company>> viewAllCompanies() {

       try{

           return Optional.of(adapter.findAllCompaniesBasic());

       } catch (Exception e) {

           System.out.println(e.getMessage());

           return Optional.empty();

       }

    }

    @GetMapping(
            value = {"/{id}"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull Optional<Company> findById(@NotNull final @PathVariable Long id) {

        try{

            return Optional.of(adapter.findCompanyById(id));

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return Optional.empty();

        }

    }

    @PostMapping
    public @NotNull Optional<Company> addCompany(@NotNull final @RequestBody Company company) {

        try {

            return Optional.of(adapter.createCompany(company));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }

    @DeleteMapping(
            value = {"/{id}"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull Optional<Company> deleteCompanyById(@NotNull final @PathVariable Long id) {

        try{

            return (adapter.deleteCompany(id));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }

    @PutMapping(
            value = {"/{id}"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull Optional<Company> updateCompany(@NotNull final @RequestBody Company company, @PathVariable Long id) throws Exception {
        try {

            return Optional.of(adapter.updateCompany(company, id));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }
}
