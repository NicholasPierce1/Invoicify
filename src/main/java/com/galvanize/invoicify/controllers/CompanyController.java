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
 * The Company Controller Class is responsible for facilitating business logic to the adapter to fulfill responses. It takes in requests
 * by request, and based off of the endpoint, it will either return to the user a list of companies, or an individual
 * company, or it will perform a create, delete, or update crud method (implemented from JPA Repository).
 * </h2>
 */
@RestController
@RequestMapping({"/api/company", "/api/company/"})
public final class CompanyController {

    /**
     * <p>
     * In order to connect a request to its corresponding response, this Adapter type adapter lays the bridge
     * from modelObject to mDataAccessObject.
     * </p>
     */
    private final Adapter adapter;

    /**
     * <p>
     * Autowired constructor that takes in the Adapter bean and renders a bean of type CompanyController.
     * </p>
     *
     * @param adapter -> preexisting bean injection servicing the remote data store
     */
    @Autowired
    public CompanyController(Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * <p>
     * A Get request is sent to: http://localhost:8080/api/company , that logic is processed and then shipped
     * to the adapter to communicate with the DataAccessObject -> and the database in turn. The response is
     * the rendered list of companies that the DataAccessObject retrieves. Important to note that if there
     * is no companies present, the application will not crash.
     * </p>
     *
     * @return : List<Company> : a compiled list of all the present companies
     */
    @GetMapping(
            value = {"/", ""},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @NotNull
    Optional<List<Company>> viewAllCompanies() {

        try {

            return Optional.of(adapter.findAllCompaniesBasic());

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return Optional.empty();

        }

    }

    /**
     * <p>
     * A Get request is sent to: http://localhost:8080/api/company/{id} in order to retrieve data from a single company.
     * the controller processes the request and sends it to the adapter. After the request has been validated and
     * processed, the specific company is returned as a json literal, if it exists; doing so without interrupting the flow of the
     * application
     * </p>
     *
     * @param id -> requires an id @PathVariable in order to search for the requested company
     * @return : Optional<Company> if that Company exists with the id, null otherwise
     */
    @GetMapping(
            value = {"/{id}", "/{id}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull
    Optional<Company> findById(@NotNull final @PathVariable Long id) {

        try {

            return Optional.of(adapter.findCompanyById(id));

        } catch (Exception e) {

            System.out.println(e.getMessage());

            return Optional.empty();

        }

    }

    /**
     * <p>
     * A Post request is sent to: http://localhost:8080/api/company in means of starting a request to add
     * a company. It is sent to the adapter which parses the user input, validates if a different
     * entry with the same name already exists, computes the logic, and adds that company to the
     * database.
     * </p>
     *
     * @param company -> requires a company @RequestBody parameter in order to add a company to the list.
     * @return : companyRepository -> saving created company
     */

    @PostMapping
    public @NotNull
    Optional<Company> addCompany(@NotNull final @RequestBody Company company) throws Exception {

        try {

            if (company.getName() == null || company.getName().equals("")) {
                throw new Exception("Sorry, cannot create a company without a name ");

            }

            return Optional.of(adapter.createCompany(company));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }

    /**
     * <p>
     * A Delete request is sent to: http://localhost:8080/api/company/{id} for searching up a particular
     * company to remove out of the company table. The request is passed on to the adapter to lookup
     * the database to see if that company does not exist first, then proceeds to pull out that particular
     * entry.
     * </p>
     *
     * @param id -> requires an id @PathVariable in order to search for the requested company to delete
     * @return : the company that was removed; should be null
     */
    @DeleteMapping(
            value = {"/{id}", "/{id}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull
    Optional<Company> deleteCompanyById(@NotNull final @PathVariable Long id) {

        try {

            return (adapter.deleteCompany(id));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }

    /**
     * <p>
     * A Put request is sent to http://localhost:8080/api/company/{companyId} to administer a need for
     * modifying an existing Company. This companyController calls the adapter to confirm that the
     * data being sent from the client does not exist in the database already, then makes the
     * adjustments as collected from the user.
     * </p>
     *
     * @param company -> requires a Company type company from the body of the request preliminarily
     * @param id      -> requires an id in the PathVariable to find the company
     * @return : the updated Company
     */
    @PutMapping(
            value = {"/{id}", "/{id}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @NotNull
    Optional<Company> updateCompany(@NotNull final @RequestBody Company company, final @PathVariable Long id) {
        try {

            if (company.getName() == null || company.getName().equals("")) {
                throw new Exception("Sorry, cannot update to a company without a name ");

            }
            return Optional.of(adapter.updateCompany(company, id));

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());

            return Optional.empty();
        }
    }
}
