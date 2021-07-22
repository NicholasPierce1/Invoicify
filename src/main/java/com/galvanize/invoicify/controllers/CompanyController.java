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
 *     The Company Controller Class is responsible for facilitating business logic to the adapter to fulfill responses. It takes in requests
 *      by request, and based off of the endpoint, it will either return to the user a list of companies, or an individual
 *      company, or it will perform a create, delete, or update crud method (implemented from JPA Repository).
 * </h2>
 */
@RestController
@RequestMapping("/api/company")
public class CompanyController  {

    /**
     * <p>
     *     In order to connect a request to its corresponding response, this Adapter type adapter lays the bridge
     *      from modelObject to mDataAccessObject.
     * </p>
     */
    private final Adapter adapter;

    /**
     * <p>
     *      This constructor takes in the Adapter and injects into the CompanyController class.
     * </p>
     * @param adapter
     */
    @Autowired
    public CompanyController(Adapter adapter){
        this.adapter = adapter;
    }

    /**
     * <p>
     *     A Get request is sent to: http://localhost:8080/api/company , that logic is processed and then shipped
     *      to the adapter to communicate with the DataAccessObject -> and the database in turn. The response is
     *      the rendered list of companies that the DataAccessObject retrieves. Important to note that if there
     *      is no companies present, the application will not crash.
     * </p>
     * @return : <List<Company>
     */
    @GetMapping
    public @NotNull Optional<List<Company>> viewAllCompanies() {

       try{

           return Optional.of(adapter.findAllCompaniesBasic());

       } catch (Exception e) {

           System.out.println(e.getMessage());

           return Optional.empty();

       }

    }

    /**
     * <p>
     *     A Get request is sent to: http://localhost:8080/api/company/{id} in order to retrieve data from a single company.
     *      the controller processes the request and sends it to the adapter. After the request has been validated and
     *      processed, the specific company is returned as a json literal, if it exists; doing so without interrupting the flow of the
     *      application
     * </p>
     * @param id -> requires an id @PathVariable in order to search for the requested company
     * @return : Company
     */
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

    /**
     * <p>
     *      A Post request is sent to: http://localhost:8080/api/company in means of starting a request to add
     *        a company. It is sent to the adapter which parses the user input, validates if a different
     *        entry with the same name already exists, computes the logic, and adds that company to the
     *        database.
     * </p>
     * @param company -> requires a company @RequestBody parameter in add a company to the list.
     * @return : companyRepository -> saving created company
     */

    @PostMapping
    public @NotNull Optional<Company> addCompany(@NotNull final @RequestBody Company company) {

        try {

            return Optional.of(adapter.createCompany(company));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }

    /**
     * <p>
     *      A Delete request is sent to: http://localhost:8080/api/company/{id} for searching 
     * </p>
     * @param id -> requires an id @PathVariable in order to search for the requested company to delete
     * @return : the company that was removed; should be null
     */
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
