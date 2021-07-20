package com.galvanize.invoicify.repository.adapter;

import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceLineItemDataAccess;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;

import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.invoicerepository.InvoiceRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Adapter {

    public final UserRepository _userRepository;
    public final  CompanyRepository _companyRepository;
    public final InvoiceRepository _invoiceRepository;

    private final PasswordEncoder _encoder;

    @Autowired
    public Adapter(UserRepository userRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder, InvoiceRepository invoiceRepository){
        this._userRepository = userRepository;
        this._companyRepository = companyRepository;
        this._encoder = passwordEncoder;
        this._invoiceRepository = invoiceRepository;

    }

    public Adapter(UserRepository userRepository, PasswordEncoder encoder) {
        this(userRepository, null, encoder, null);
    }

    public Adapter(InvoiceRepository invoiceRepository) {
        this(null,null,null,invoiceRepository);
    }


    // ...stubs go below
    // add your method signatures to complete your user stories here

    public @NotNull Optional<User> getUserByUserName(final String username){

        final Optional<UserDataAccess> userDataAccessOptional = this._userRepository.findByUsername(username);

        return userDataAccessOptional.map(userDataAccess -> userDataAccess.convertTo(User::new));

    }


    public User updateUser(User user, Long id) throws DuplicateUserException {
        UserDataAccess currentUserData = this._userRepository.findById(id).get();

        if (user.getUsername() != null && !user.getUsername().equals("")) {
            //check if there's another user with the given username and prevent duplication of user ids.
            if (isUserExists(user.getUsername())){
                throw new DuplicateUserException("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
            }
            currentUserData.setUsername(user.getUsername());
        }

        if (user.getPassword() != null && !user.getPassword().equals("")) {
            currentUserData.setPassword(_encoder.encode(user.getPassword()));
        }

        return _userRepository.save(currentUserData).convertTo((User::new));
    }

    public User createUser(User user) throws DuplicateUserException {
        if (isUserExists(user.getUsername())){
            throw new DuplicateUserException("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
        }
        UserDataAccess userDataAccess = new UserDataAccess();
        userDataAccess.setUsername(user.getUsername());
        userDataAccess.setPassword(_encoder.encode(user.getPassword()));
        return _userRepository.save(userDataAccess).convertTo((User::new));
    }

    private boolean isUserExists(String userName) throws DuplicateUserException {
        int userCountByUsername = this._userRepository.countUsersByUserName(userName);
        return userCountByUsername > 0;
    }


    public List<User> findAll() {
        return _userRepository.findAll().stream().map(userDataAccess -> userDataAccess.convertTo(User::new)).collect(Collectors.toList());
    }

    public User findUser(Long id) {
        return _userRepository.findById(id).map(userDataAccess -> userDataAccess.convertTo(User::new)).get();
    }

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

    public Invoice createInvoice(Invoice invoice, long companyId, User user) {
        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess();
        invoiceDataAccess.setCompanyId(companyId);
        invoiceDataAccess.setCreatedOn(new Date());
        //invoiceDataAccess.setCreatedBy(user.getId());
        invoiceDataAccess.setDescription(invoice.getInvoiceDescription());

        createInvoiceItem(invoice.getRecordIds());
        return this._invoiceRepository.save(invoiceDataAccess).convertTo(Invoice::new);
    }

    private void createInvoiceItem(List<String> recordIds) {
        InvoiceLineItemDataAccess invoiceLineItemDataAccess = new InvoiceLineItemDataAccess();
    }
}
