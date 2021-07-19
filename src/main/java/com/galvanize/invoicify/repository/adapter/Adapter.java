package com.galvanize.invoicify.repository.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Adapter {

    public final UserRepository _userRepository;

    public final  CompanyRepository _companyRepository;

    public final FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    public final RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    private final PasswordEncoder _encoder;

    @Autowired
    public Adapter(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            FlatFeeBillingRecordRepository flatFeeBillingRecordRepository,
            RateBaseBillingRecordRepository rateBaseBillingRecordRepository,
            PasswordEncoder passwordEncoder){
        this._userRepository = userRepository;
        this._companyRepository = companyRepository;
        this._flatFeeBillingRecordRepository = flatFeeBillingRecordRepository;
        this._rateBasedBillingRecordRepository = rateBaseBillingRecordRepository;
        this._encoder = passwordEncoder;
    }


    // ...stubs go below
    // add your method signatures to complete your user stories here

    public @NotNull Optional<User> getUserByUserName(final String username){

        final Optional<UserDataAccess> userDataAccessOptional = this._userRepository.findByUsername(username);

        return userDataAccessOptional.map(userDataAccess -> userDataAccess.convertTo(User::new));

    }


    public User updateUser(User user, Long id) throws DuplicateUserException {
        UserDataAccess currentUserData = this._userRepository.findById(id).get();//orElseThrow(DuplicateUserException::new);

        if (user.getUsername() != null || !user.getUsername().equals("")) {
            //check if there's another user with the given username and prevent duplication of user ids.
            if (isUserExists(user.getUsername())){
                throw new DuplicateUserException("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
            }
            currentUserData.setUsername(user.getUsername());
        }

        if (user.getPassword() != null || !user.getPassword().equals("")) {
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

    public @NotNull List<BillingRecord> getAllBillingRecords(){

        // holds conjoined billing records (flat fee & rate base)
        final List<BillingRecord> billingRecords = new ArrayList<BillingRecord>();

        // gets all flat fees, converts from DA -> Model, and appends to billing records
        billingRecords.addAll(this._flatFeeBillingRecordRepository
                .findAll()
                .stream()
                .map(
                        (billingRecordDataAccess) -> billingRecordDataAccess.convertTo(FlatFeeBillingRecord::new)
                )
                .collect(Collectors.toList())
        );

        // gets all rate based fees, converts from DA -> Model, and appends to billing records
        billingRecords.addAll(this._rateBasedBillingRecordRepository
                .findAll()
                .stream()
                .map(
                        (billingRecordDataAccess) -> billingRecordDataAccess.convertTo(RateBasedBillingRecord::new)
                )
                .collect(Collectors.toList())
        );

        return billingRecords;

    }

    public @NotNull Optional<BillingRecord> getBillingRecordById(@NotNull final Long id){
        final ObjectMapper objectMapper = new ObjectMapper();
        // note: billing record may be in Flat, Rate, or none
        Optional<BillingRecord> billingRecord;

        // checking if flat fee retains billing record
        billingRecord = this.
                _flatFeeBillingRecordRepository.findById(id)
                .map(
                        (flatFeeBillingRecordDataAccess -> flatFeeBillingRecordDataAccess.convertTo(FlatFeeBillingRecord::new))
                );
        try {
            System.out.println(objectMapper.writeValueAsString(billingRecord.get()));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        if(billingRecord.isPresent())
            return billingRecord;

        // check if rate base retains billing record
        // returns regardless if present or not
        return this.
                _rateBasedBillingRecordRepository.findById(id)
                .map(
                        (flatFeeBillingRecordDataAccess -> flatFeeBillingRecordDataAccess.convertTo(RateBasedBillingRecord::new))
                );

    }
}
