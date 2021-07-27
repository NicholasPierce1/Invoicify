package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.controllers.CompanyController;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h2>
 * todo: write description here
 * </h2>
 */
@Service
public final class Adapter {

    public final UserRepository _userRepository;

    public final CompanyRepository _companyRepository;

    private final PasswordEncoder _encoder;

    @Autowired
    public Adapter(UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this._userRepository = userRepository;
        this._companyRepository = companyRepository;
        this._encoder = passwordEncoder;
    }

    // ...stubs go below
    // add your method signatures to complete your user stories here

    /**
     * <p>
     * Conveys the logic requested by the controller. It reads all the companies from the table, collects the
     * DataAccessObjects, and converts each of them into a Company Model and persists them to a list.
     * </p>
     *
     * @return : List<Company> -> finds all Company DataAccessObjects, streams and maps them together, converts
     * each into a Company Model Object, then returns it as a list
     */

    public @NotNull
    List<Company> findAllCompaniesBasic() {
        return this._companyRepository
                .findAll()
                .stream()
                .map((companyDataAccess) -> companyDataAccess.convertToModel(Company::new))
                .collect(Collectors.toList());
    }

    /**
     * <p>
     * Handles the request from the controller by utilizing the companyRepository; looks up the company with the given
     * id and then converts the DataAccessObject to a Company Model.
     * </p>
     *
     * @param id -> requires an id in order to search for the requested company
     * @return : Company -> uses companyRepository to find the specified company DotaAccessObject by its id and converts
     * it to a Company Model Object.
     */
    public @NotNull
    Company findCompanyById(@NotNull final long id) {

        return this._companyRepository
                .findById(id)
                .map(companyDataAccess -> companyDataAccess.convertToModel(Company::new)).get();
    }

    /**
     * <p>
     * Handles the request from the controller by utilizing the companyRepository; it cross checks existing company
     * names to verify that the company does not already exist in the company table. Then saves the company as a
     * DataAccessObject in the company table and returns it as the converted Company Model.
     * </p>
     *
     * @param company -> takes in a Company company specified by the user
     * @return : Company -> With the companyRepository, it checks if the company already exists, throws a DuplicateCompanyException,
     * otherwise, a new company DataAccessObject is instantiated then converted to a Company Model
     * @throws DuplicateCompanyException
     */
    public @NotNull
    Company createCompany(@NotNull final Company company) throws Exception {

        if (this._companyRepository.findByName(company.getName()).isPresent()) {
            throw new DuplicateCompanyException("Sorry " + company.getName() + " already exists. Give it another name");
        }

        CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.convertToDataAccess(company);

        return _companyRepository
                .save(companyDataAccess)
                .convertToModel(Company::new);


    }

    /**
     * <p>
     * Once the delete request has been sent from the controller, an Optional<Company> company Object is sent to the
     * companyRepository to find the corresponding DataAccessObject and converts it to the Company Model. If that
     * company is not present, an error message prompts user to change their input. If the company does exist,
     * then it will be stored in a temporary variable, deleted from the database and returned to the user.
     * </p>
     *
     * @param id -> specified company id to delete
     * @return : Optional<Company> -> Validates if the specified company exists. If the company does not exist,
     * it returns an empty Optional. If it does, then it calls companyRepository to delete it and returns the
     * deleted company.
     */
    public @NotNull
    Optional<Company> deleteCompany(@NotNull final Long id) throws Exception{

        final Optional<Company> company = this._companyRepository.findById(id).map(companyDataAccess -> companyDataAccess.convertToModel(Company::new));

        if (company.isPresent()) {

            _companyRepository.deleteById(id);
        }

        else {

            throw new Exception("Cannot delete a company that doesn't exist");

        }

        return company;

    }

    /**
     * <p>
     * When the request gets rendered, a DataAccessObject, currentCompanyData, is set to the companyRepository to
     * find and retrieve the corresponding company DataAccessObject. If the company DataAccessObject does exists
     * in the table, the user is prompted to change the name of the company. If the company DataAccessObject does not
     * exists in the table, the company repository saves the new properties of the specified company, converts the
     * company DataAccessObject to a company Model and returns it the the user.
     *
     * </p>
     *
     * @param company -> Requires the base company that is being updated.
     * @param id      -> Requires the serialized id of the company being updated.
     * @return : Company -> Calls the companyRepository to save the modified company DataAccessObject, then converts to
     * a Company Model.
     */
    public @NotNull
    Company updateCompany(@NotNull final Company company, Long id) throws Exception{

        CompanyDataAccess currentCompanyData = this._companyRepository.findById(id).get();

        if (this._companyRepository.findByName(company.getName()).isPresent()) {

            throw new DuplicateCompanyException("Company " + company.getName() + "is an existing company name. Please choose a different name.");
        }

        currentCompanyData.setName(company.getName());

        return _companyRepository.save(currentCompanyData).convertToModel((Company::new));

    }
}
