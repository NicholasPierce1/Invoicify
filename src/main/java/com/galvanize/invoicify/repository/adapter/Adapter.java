package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.controllers.CompanyController;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class Adapter {

    public final UserRepository _userRepository;

    public final  CompanyRepository _companyRepository;

    private final PasswordEncoder _encoder;

    @Autowired
    public Adapter(UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder){
        this._userRepository = userRepository;
        this._companyRepository = companyRepository;
        this._encoder = passwordEncoder;
    }

    // ...stubs go below
    // add your method signatures to complete your user stories here

    public List<Company> findAllCompaniesBasic(){
        return this._companyRepository
                .findAll()
                .stream()
                .map( (companyDataAccess) -> companyDataAccess.convertToModel(Company::new) )
                .collect(Collectors.toList());
    }

    public Company findCompanyById(long id) {

        return this._companyRepository
                .findById(id)
                .map(companyDataAccess -> companyDataAccess.convertToModel(Company::new)).get();
    }

    public Company createCompany(Company company) throws DuplicateCompanyException{

        if (this._companyRepository.findByName(company.getName()).isPresent()) {
            throw new DuplicateCompanyException ("Sorry " + company.getName() + " already exists. Give it another name");

        }

        CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName(company.getName());

        return _companyRepository
                        .save(companyDataAccess)
                        .convertToModel(Company::new);

    }
    public Optional<Company> deleteCompany(Long id)  {

    final Optional<Company> company = this._companyRepository.findById(id).map(companyDataAccess -> companyDataAccess.convertToModel(Company::new));

    if(company.isPresent())
        _companyRepository.deleteById(id);


    return company;

    }

    /**
     *
     * @param company
     * @param id
     * @return
     * @throws DuplicateCompanyException if blah blah
     */
    public Company updateCompany(Company company, Long id) {
        //todo: add comments
        CompanyDataAccess currentCompanyData = this._companyRepository.findById(id).get();

        if(company.getName() != null && !company.getName().equals("")){
            if(this._companyRepository.findByName(company.getName()).isPresent()){
                throw new DuplicateCompanyException("Company " + company.getName() + "is an existing company name. Please choose a different name.");
            }
            currentCompanyData.setName(company.getName());
        }
        return _companyRepository.save(currentCompanyData).convertToModel((Company::new));
    }
}
