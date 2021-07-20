package com.galvanize.invoicify.repository.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class Adapter {

    public final  CompanyRepository _companyRepository;

    public final FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    public final RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    private final PasswordEncoder _encoder;

    private final BillingRecordParentHelper _billingRecordParentHelper;

    public final UserRepository _userRepository;

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
        this._billingRecordParentHelper = new BillingRecordParentHelper(this);
    }


    // ...stubs go below
    // add your method signatures to complete your user stories here

    public @NotNull Optional<User> getUserByUserName(final String username){

        final Optional<UserDataAccess> userDataAccessOptional = this._userRepository.findByUsername(username);

        return userDataAccessOptional.map(userDataAccess -> userDataAccess.convertToModel(User::new));

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

        return _userRepository.save(currentUserData).convertToModel((User::new));
    }

    public User createUser(User user) throws DuplicateUserException {
        if (isUserExists(user.getUsername())){
            throw new DuplicateUserException("Username " + user.getUsername() + " already exists. Please choose another username to update your account to." );
        }
        UserDataAccess userDataAccess = new UserDataAccess();
        userDataAccess.setUsername(user.getUsername());
        userDataAccess.setPassword(_encoder.encode(user.getPassword()));
        return _userRepository.save(userDataAccess).convertToModel((User::new));
    }

    private boolean isUserExists(String userName) throws DuplicateUserException {
        int userCountByUsername = this._userRepository.countUsersByUserName(userName);
        return userCountByUsername > 0;
    }


    public List<User> findAll() {
        return _userRepository.findAll().stream().map(userDataAccess -> userDataAccess.convertToModel(User::new)).collect(Collectors.toList());
    }

    public User findUser(Long id) {
        return _userRepository.findById(id).map(userDataAccess -> userDataAccess.convertToModel(User::new)).get();
    }

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

    public @NotNull List<BillingRecord> getAllBillingRecords(){

        // holds conjoined billing records (flat fee & rate base)
        final List<BillingRecord> billingRecords = new ArrayList<BillingRecord>();

        // gets all flat fees, converts from DA -> Model, and appends to billing records
        billingRecords.addAll(this._flatFeeBillingRecordRepository
                .findAll()
                .stream()
                .map(
                        (flatFeeBillingRecordDataAccess) -> {

                            // gets user and company
                            // note: assumes user and company exist in this circumstance
                            final Optional<Pair<Company, User>> companyUserPair = this
                                    ._billingRecordParentHelper
                                    .getCompanyAndClient(
                                            flatFeeBillingRecordDataAccess.getCompanyId(),
                                            flatFeeBillingRecordDataAccess.getCreatedBy()
                                    );

                            if(!companyUserPair.isPresent())
                                throw new RuntimeException("company or user don't exist for this billing record. Was it deleted?");

                            flatFeeBillingRecordDataAccess.setCompany(companyUserPair.get().getValue0());
                            flatFeeBillingRecordDataAccess.setUser(companyUserPair.get().getValue1());

                            return flatFeeBillingRecordDataAccess.convertToModel(FlatFeeBillingRecord::new);
                        }
                )
                .collect(Collectors.toList())
        );

        // gets all rate based fees, converts from DA -> Model, and appends to billing records
        billingRecords.addAll(this._rateBasedBillingRecordRepository
                .findAll()
                .stream()
                .map(
                        (rateBasedBillingRecordDataAccess) -> {

                            // gets user and company
                            // note: assumes user and company exist in this circumstance
                            final Optional<Pair<Company, User>> companyUserPair = this
                                    ._billingRecordParentHelper
                                    .getCompanyAndClient(
                                            rateBasedBillingRecordDataAccess.getCompanyId(),
                                            rateBasedBillingRecordDataAccess.getCreatedBy()
                                    );

                            if(!companyUserPair.isPresent())
                                throw new RuntimeException("company or user don't exist for this billing record. Was it deleted?");

                            rateBasedBillingRecordDataAccess.setCompany(companyUserPair.get().getValue0());
                            rateBasedBillingRecordDataAccess.setUser(companyUserPair.get().getValue1());

                            return rateBasedBillingRecordDataAccess.convertToModel(RateBasedBillingRecord::new);
                        }
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
                        (flatFeeBillingRecordDataAccess) -> {

                            // gets user and company
                            // note: assumes user and company exist in this circumstance
                            final Optional<Pair<Company, User>> companyUserPair = this
                                    ._billingRecordParentHelper
                                    .getCompanyAndClient(
                                            flatFeeBillingRecordDataAccess.getCompanyId(),
                                            flatFeeBillingRecordDataAccess.getCreatedBy()
                                    );

                            if(!companyUserPair.isPresent())
                                throw new RuntimeException("company or user don't exist for this billing record. Was it deleted?");

                            flatFeeBillingRecordDataAccess.setCompany(companyUserPair.get().getValue0());
                            flatFeeBillingRecordDataAccess.setUser(companyUserPair.get().getValue1());

                            return flatFeeBillingRecordDataAccess.convertToModel(FlatFeeBillingRecord::new);
                        }
                );

        if(billingRecord.isPresent())
            return billingRecord;

        // check if rate base retains billing record
        // returns regardless if present or not
        return this.
                _rateBasedBillingRecordRepository
                .findById(id)
                .map(
                        (rateBasedBillingRecordDataAccess) -> {

                            // gets user and company
                            // note: assumes user and company exist in this circumstance
                            final Optional<Pair<Company, User>> companyUserPair = this
                                    ._billingRecordParentHelper
                                    .getCompanyAndClient(
                                            rateBasedBillingRecordDataAccess.getCompanyId(),
                                            rateBasedBillingRecordDataAccess.getCreatedBy()
                                    );

                            if(!companyUserPair.isPresent())
                                throw new RuntimeException("company or user don't exist for this billing record. Was it deleted?");

                            rateBasedBillingRecordDataAccess.setCompany(companyUserPair.get().getValue0());
                            rateBasedBillingRecordDataAccess.setUser(companyUserPair.get().getValue1());

                            return rateBasedBillingRecordDataAccess.convertToModel(RateBasedBillingRecord::new);
                        }
                );

    }

    public @NotNull Optional<FlatFeeBillingRecord> saveFlatFeeBillingRecord(@NotNull FlatFeeBillingRecord flatFeeBillingRecord) throws Exception{

        // convert to data access object
        final FlatFeeBillingRecordDataAccess flatFeeBillingRecordDataAccess = new FlatFeeBillingRecordDataAccess();
        flatFeeBillingRecordDataAccess.convertToDataAccess(flatFeeBillingRecord);

        final ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(flatFeeBillingRecordDataAccess));

        return Optional.empty();

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

    private static class BillingRecordParentHelper{

        private final Adapter _adapter;

        public BillingRecordParentHelper(@NotNull final Adapter adapter){
            this._adapter = adapter;
        }

        public Optional<Pair<Company, User>> getCompanyAndClient(final long companyId, final long userId){

            final Optional<User> user = this.getUserById(userId);

            final Optional<Company> company = this.getCompanyById(companyId);

            return user.isPresent() && company.isPresent() ?
                    Optional.of(new Pair<Company, User>(company.get(), user.get()))
                    :
                    Optional.empty();

        }

        public Optional<User> getUserById(final long clientId){
            return this
                    ._adapter
                    ._userRepository
                    .findById(clientId)
                    .map( (userDataAccess -> userDataAccess.convertToModel(User::new)) );
        }

        public Optional<Company> getCompanyById(final long companyId){
            return this
                    ._adapter
                    ._companyRepository
                    .findById(companyId)
                    .map( (companyDataAccess -> companyDataAccess.convertToModel(Company::new)) );
        }

    }

}
