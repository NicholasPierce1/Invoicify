package com.galvanize.invoicify.repository.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
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
        // getting user by id --- user exists in database guaranteed
        UserDataAccess currentUserData = this._userRepository.findById(id).get();

        if (user.getUsername() != null && !user.getUsername().equals("")) {
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

    /**
     * <p>
     *     Exposed adapter stub for BillingRecordRepository endpoint to
     *     encapsulate the retrieval of all BillingRecords with children state attached.
     * </p>
     * @return List of BillingRecords with children state attached
     */
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

    /**
     * <p>
     *     Exposed adapter stub for BillingRecordRepository endpoint to
     *     encapsulate the retrieval of a BillingRecord via its ID with children state attached.
     * </p>
     * @param id: a NotNull ID that maps to either a FlatFeeBillingRecord or a RateBasedBillingRecord
     * @return an Optional BillingRecord that maps to the given ID. Null otherwise.
     */
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

    /**
     * <p>
     *     Exposed adapter stub for BillingRecordRepository endpoint to
     *     encapsulate the saving of a FlatFeeBillingRecord.
     * </p>
     * @param flatFeeBillingRecord: a non-null composite FlatFeeBillingRecord to save.
     *                            Composite: ID field not attached but children IDs are attached.
     * @return an Optional FlatFeeBillingRecord with children information fully qualified.
     * If the children IDs do not map to a preexisting child entity then empty Optional.
     */
    public @NotNull Optional<FlatFeeBillingRecord> saveFlatFeeBillingRecord(
            @NotNull final FlatFeeBillingRecord flatFeeBillingRecord){

        // convert to data access object
        FlatFeeBillingRecordDataAccess flatFeeBillingRecordDataAccess = new FlatFeeBillingRecordDataAccess();
        flatFeeBillingRecordDataAccess.convertToDataAccess(flatFeeBillingRecord);

        // acquires client & company
        final Optional<User> user = this
                ._userRepository
                .findById(
                        flatFeeBillingRecord
                                .getCreatedBy()
                                .getId()
                )
                .map(
                        (userDataAccess -> userDataAccess.convertToModel(User::new))
                );

        final Optional<Company> company = this
                ._companyRepository
                .findById(
                        flatFeeBillingRecord
                                .getCreatedBy()
                                .getId()
                )
                .map(
                        (companyDataAccess -> companyDataAccess.convertToModel(Company::new))
                );

        // verifies that user and company exist
        if(!user.isPresent() || !company.isPresent() || flatFeeBillingRecord.getId() != null)
            return Optional.empty();

        // save flat fee object
        flatFeeBillingRecord.setId(
                this.
                        _flatFeeBillingRecordRepository
                        .save(flatFeeBillingRecordDataAccess)
                        .getId()
        );

        // sets client and company
        flatFeeBillingRecord.setClient(company.get());
        flatFeeBillingRecord.setCreatedBy(user.get());

        // saves flat fee
        return Optional.of(flatFeeBillingRecord);
    }

    /**
     * <p>
     *     Exposed adapter stub for BillingRecordRepository endpoint to
     *     encapsulate the saving of a RateBasedBillingRecord.
     * </p>
     * @param rateBasedBillingRecord: a non-null composite RateBasedBillingRecord to save.
     *                            Composite: ID field not attached but children IDs are attached.
     * @return an Optional RateBasedBillingRecord with children information fully qualified.
     * If the children IDs do not map to a preexisting child entity then empty Optional.
     */
    public @NotNull Optional<RateBasedBillingRecord> saveRateBasedFeeBillingRecord(
            @NotNull final RateBasedBillingRecord rateBasedBillingRecord){

        // convert to data access object
        RateBasedBillingRecordDataAccess rateBasedBillingRecordDataAccess = new RateBasedBillingRecordDataAccess();
        rateBasedBillingRecordDataAccess.convertToDataAccess(rateBasedBillingRecord);

        // acquires client & company
        final Optional<User> user = this
                ._userRepository
                .findById(
                        rateBasedBillingRecord
                                .getCreatedBy()
                                .getId()
                )
                .map(
                        (userDataAccess -> userDataAccess.convertToModel(User::new))
                );

        final Optional<Company> company = this
                ._companyRepository
                .findById(
                        rateBasedBillingRecord
                                .getCreatedBy()
                                .getId()
                )
                .map(
                        (companyDataAccess -> companyDataAccess.convertToModel(Company::new))
                );

        // verifies that user and company exist
        if(!user.isPresent() || !company.isPresent() || rateBasedBillingRecord.getId() != null)
            return Optional.empty();

        // save flat fee object
        rateBasedBillingRecord.setId(
                this.
                        _rateBasedBillingRecordRepository
                        .save(rateBasedBillingRecordDataAccess)
                        .getId()
        );

        // sets client and company
        rateBasedBillingRecord.setClient(company.get());
        rateBasedBillingRecord.setCreatedBy(user.get());

        // saves flat fee
        return Optional.of(rateBasedBillingRecord);
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

    public Company updateCompany(Company company, Long id) {
        CompanyDataAccess currentCompanyData = this._companyRepository.findById(id).get();

        if(company.getName() != null && !company.getName().equals("")){
            if(this._companyRepository.findByName(company.getName()).isPresent()){
                throw new DuplicateCompanyException("Company " + company.getName() + "is an existing company name. Please choose a different name.");
            }
            currentCompanyData.setName(company.getName());
        }
        return _companyRepository.save(currentCompanyData).convertToModel((Company::new));
    }

    /**
     * <h2>
     *     BillingRecordParentHelper
     * </h2>
     * <p>
     *     Static helper class to facilitate operations with children retrieval and
     *     qualification.
     * </p>
     */
    private static class BillingRecordParentHelper{

        /**
         * <p>
         * Bean injection of adapter passed via non-autowired constructor to
         * have access to adapter stubs.
         * Usage: invoke Company and User repository endpoints for children entity retrievals and qualification
         * for BillingRecords
         * </p>
         */
        private final Adapter _adapter;

        /**
         * <p>
         *     Non-autowired constructor to attached adapter state for internal, repository invocation.
         * </p>
         * @param adapter: Bean injected adapter.
         */
        public BillingRecordParentHelper(@NotNull final Adapter adapter){
            this._adapter = adapter;
        }

        /**
         * <p>
         *     Retrieves Company and User (pre-converted to model endpoint) via their respective IDs.
         * </p>
         * @param companyId: ID endpoint that maps to preexisting Company
         * @param userId: ID endpoint that maps to preexisting User
         * @return An Optional Pair retaining both model objects if they exist.
         * If either, or both, IDs do not map to their respective children then an empty Optional is given.
         */
        public Optional<Pair<Company, User>> getCompanyAndClient(final long companyId, final long userId){

            final Optional<User> user = this.getUserById(userId);

            final Optional<Company> company = this.getCompanyById(companyId);

            return user.isPresent() && company.isPresent() ?
                    Optional.of(new Pair<Company, User>(company.get(), user.get()))
                    :
                    Optional.empty();

        }

        /**
         * <p>
         *     Retrieves an User (pre-converted to model endpoint) via its respective ID.
         * </p>
         * @param clientId: ID endpoint that maps to preexisting User
         * @return An Optional User
         * If ID does not map to its respective endpoint then an empty Optional is given.
         */
        public Optional<User> getUserById(final long clientId){
            return this
                    ._adapter
                    ._userRepository
                    .findById(clientId)
                    .map( (userDataAccess -> userDataAccess.convertToModel(User::new)) );
        }

        /**
         * <p>
         *     Retrieves a Company (pre-converted to model endpoint) via its respective ID.
         * </p>
         * @param companyId: ID endpoint that maps to preexisting User
         * @return An Optional Company
         * If ID does not map to its respective endpoint then an empty Optional is given.
         */
        public Optional<Company> getCompanyById(final long companyId){
            return this
                    ._adapter
                    ._companyRepository
                    .findById(companyId)
                    .map( (companyDataAccess -> companyDataAccess.convertToModel(Company::new)) );
        }

    }

}
