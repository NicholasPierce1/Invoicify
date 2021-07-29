package com.galvanize.invoicify.repository.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.dataaccess.*;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.repositories.companyrepository.CompanyRepository;
import com.galvanize.invoicify.repository.repositories.flatfeebillingrecord.FlatFeeBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.invoicelineitemrepository.InvoiceLineItemRepository;
import com.galvanize.invoicify.repository.repositories.invoicerepository.InvoiceRepository;
import com.galvanize.invoicify.repository.repositories.ratebasebillingrecord.RateBaseBillingRecordRepository;
import com.galvanize.invoicify.repository.repositories.userrepository.UserRepository;
import com.sun.istack.NotNull;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h2>
 *   Adapter
 * </h2>
 * <p>
 *     //todo: describe here
 * </p>
 */
@Service
public final class Adapter {


    public final  CompanyRepository _companyRepository;

    public final InvoiceRepository _invoiceRepository;

    public final InvoiceLineItemRepository _invoiceLineItemRepository;

    public final UserRepository _userRepository;

    public final FlatFeeBillingRecordRepository _flatFeeBillingRecordRepository;

    public final RateBaseBillingRecordRepository _rateBasedBillingRecordRepository;

    private final PasswordEncoder _encoder;

    private final BillingRecordParentHelper _billingRecordParentHelper;

    @Autowired
    public Adapter(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            FlatFeeBillingRecordRepository flatFeeBillingRecordRepository,
            RateBaseBillingRecordRepository rateBaseBillingRecordRepository,
            InvoiceRepository invoiceRepository,
            InvoiceLineItemRepository invoiceLineItemRepository,
            PasswordEncoder passwordEncoder){
        this._userRepository = userRepository;
        this._companyRepository = companyRepository;
        this._flatFeeBillingRecordRepository = flatFeeBillingRecordRepository;
        this._rateBasedBillingRecordRepository = rateBaseBillingRecordRepository;
        this._encoder = passwordEncoder;
        this._billingRecordParentHelper = new BillingRecordParentHelper(this);
        this._invoiceRepository = invoiceRepository;
        this._invoiceLineItemRepository = invoiceLineItemRepository;
    }


    // ...stubs go below
    // add your method signatures to complete your user stories here

    /**
     * <p>
     * Conveys the logic requested by the controller. It reads all the companies from the table, collects the
     * DataAccessObjects, and converts each of them into a Company Model and persists them to a list.
     * </p>
     *
     * @return : List<Company> finds all Company DataAccessObjects, streams and maps them together, converts
     * each into a Company Model Object, then returns it as a list
     */
    public @NotNull
    List<Company> findAllCompaniesBasic(){
        return this._companyRepository
                .findAll()
                .stream()
                .map((companyDataAccess) -> companyDataAccess.convertToModel(Company::new))
                .collect(Collectors.toList());
    }


    /**
     * <p>
     *      Handles the request from the controller by utilizing the userRepository; looks up the
     *      user with the given name then converts the UserDataAccessModel to a User Model.
     * </p>
     * @param username requires a String username from the request body prior to retrieving a user by username
     * @return Optional<User> : uses userRepository to find the specified User DataAccessObject by its name and converts
     *  it to a User Model Object.
     * */
    public @NotNull Optional<User> getUserByUserName(final String username){

        final Optional<UserDataAccess> userDataAccessOptional = this._userRepository.findByUsername(username);

        return userDataAccessOptional.map(userDataAccess -> userDataAccess.convertToModel(User::new));

    }

    /**
     *<p>
     *     Handles the request from the controller by utilizing the userRepository; it locates the user to be updated
     *     by the given ID, then cross checks the given userName with existing userNames to prevent duplication of user ids.
     *     The userName is updated to the given userName, which then verifies the password is not null or blank.
     *     Then saves the user as a DAO in the user table and converts the DAO to a user Model.
     *</p>
     * @param user -> requires a String username from the request body prior to retrieving a user by username
     * @param id -> requires an id in order to search for the requested user
     * @return User : With the userRepository, it checks if the user already exists, throws a DuplicateCompanyException,
     *  otherwise, a new User DataAccessObject is instantiated then converted to a User Model
     * */

    public  @NotNull User updateUser(@NotNull final User user, Long id) throws DuplicateUserException {
        // getting user by id --- user exists in database guaranteed
        UserDataAccess currentUserData = this._userRepository.findById(id).get();

        final Optional<User> userWithUserName =
                this._userRepository.findByUsername(user.getUsername())
                .map(
                        (userDataAccess -> userDataAccess.convertToModel(User::new))
                );

        if (userWithUserName.isPresent() && !userWithUserName.get().getId().equals(id) ) {

            throw new DuplicateUserException("User " + user.getUsername() + " is an existing user name. Please choose a different name. " );
        }

        currentUserData.setUsername(user.getUsername());

        return _userRepository.save(currentUserData).convertToModel((User::new));
    }

    /**
     *<p>
     *     Handles the request from the controller by utilizing the userRepository; it cross checks existing userName
     *     with to verify that the given userName does not already exist in the user table.
     *     Then saves the user as a DAO in the user table and converts the DAO to a user Model.
     *</p>
     * @param user takes in the specific user provided
     * @return User : With the userRepository, it checks if the user exists, throws a DuplicateUserExecption,
     * otherwise, a new user DAO is instantiated then converted to a user model.
     * @throws DuplicateUserException : It addresses the issue of computing logic against duplication
     * of keys. Since the User table must contain non-null, unique String name entries,
     * this exception prevents the user from assigning a name to a User that already exists
     * in the table. This also handles redirecting the user in these instances and
     * prompting to adjust serialization so table integrity in tact and aligned with the rest of the system
     * */

    public @NotNull User createUser(@NotNull final User user) throws Exception {

        if(this._userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateUserException("Sorry " + user.getUsername() + "already exists. Please provide another user name");
        }

        UserDataAccess userDataAccess = new UserDataAccess();
        userDataAccess.setUsername(user.getUsername());
        userDataAccess.setPassword(_encoder.encode(user.getPassword()));
        return _userRepository.save(userDataAccess).convertToModel((User::new));
    }

    /**
     *<p>
     *      Handles the request from the controller by utilizing userRepository; it counts the user with the given
     *      userName to validate if the specified user exists. If it does, it returns true as a boolean.
     *</p>
     * @param userName specific user to be verified to exist in data store
     * @return boolean validates if specified user exists, then counts the number of times the userName is listed to
     * verify that the user exists. If it does exist, it returns true.
     * @exception DuplicateUserException addresses the issue of computing logic against duplication of keys.
     * Since the User table must contain non-null, unique String name entries, this exception prevents the user from
     * assigning a name to a User that already exists in the table. This also handles redirecting the user in these
     * instances and prompting to adjust serialization so table integrity in tact and aligned with the rest of the system
     * */

    private @NotNull boolean isUserExists(@NotNull final String userName) throws DuplicateUserException {
        return this._userRepository.findByUsername(userName).isPresent();
    }


    /**
     * <p>
     *     Conveys the logic requested by the controller. It reads all users from
     *     the table, collects the DAO, and converts each of them into a User model
     *     and persists them into a list.
     * </p>
     * @return List<User> : finds all the users, streams and maps them together,
     * and converts each into a User model. Then returns it as a list.
     */
    public List<User> findAll() {
        return _userRepository
                .findAll()
                .stream()
                .map(
                        userDataAccess -> userDataAccess.convertToModel(User::new)
                ).collect(Collectors.toList());
    }

    /**
     * <p>
     *     Handles the request from the controller by looking up the user with the
     *     provided id, then converts the DAO to a User Model.
     * </p>
     * @param id requires and id param for the requested user
     * @return : User finds the specific user DAO by ID and converts it
     * to a user Model.
     */
    public User findUser(Long id) {
        return _userRepository
                .findById(id)
                .map(userDataAccess -> userDataAccess.convertToModel(User::new)
                ).get();
    }

    /**
     * <p>
     * Handles the request from the controller by utilizing the companyRepository; looks up the company with the given
     * id and then converts the DataAccessObject to a Company Model.
     * </p>
     *
     * @param id requires an id in order to search for the requested company
     * @return : Company uses companyRepository to find the specified company DotaAccessObject by its id and converts
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
                            final Optional<Pair<CompanyDataAccess, UserDataAccess>> companyUserPair = this
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
                            final Optional<Pair<CompanyDataAccess, UserDataAccess>> companyUserPair = this
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
                            final Optional<Pair<CompanyDataAccess, UserDataAccess>> companyUserPair = this
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
                            final Optional<Pair<CompanyDataAccess, UserDataAccess>> companyUserPair = this
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
                                .getClient()
                                .getId()
                )
                .map(
                        (companyDataAccess -> companyDataAccess.convertToModel(Company::new))
                );

        // verifies that user and company exist
        if(!user.isPresent() || !company.isPresent())
            return Optional.empty();

        flatFeeBillingRecordDataAccess.setId(this._billingRecordParentHelper.getNextBillingRecordId());

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
                                .getClient()
                                .getId()
                )
                .map(
                        (companyDataAccess -> companyDataAccess.convertToModel(Company::new))
                );

        // verifies that user and company exist
        if(!user.isPresent() || !company.isPresent())
            return Optional.empty();

        rateBasedBillingRecordDataAccess.setId(this._billingRecordParentHelper.getNextBillingRecordId());

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


    /**
     * <p>
     * Handles the request from the controller by utilizing the companyRepository; it cross checks existing company
     * names to verify that the company does not already exist in the company table. Then saves the company as a
     * DataAccessObject in the company table and returns it as the converted Company Model.
     * </p>
     *
     * @param company takes in a Company company specified by the user
     * @return : Company With the companyRepository, it checks if the company already exists, throws a DuplicateCompanyException,
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
     * @param id specified company id to delete
     * @return : Optional<Company> Validates if the specified company exists. If the company does not exist,
     * it returns an empty Optional. If it does, then it calls companyRepository to delete it and returns the
     * deleted company.
     */
    public @NotNull
    Optional<Company> deleteCompany(@NotNull final Long id) throws Exception{

        final Optional<Company> company = this._companyRepository.findById(id).map(companyDataAccess -> companyDataAccess.convertToModel(Company::new));

        if(company.isPresent())
            _companyRepository.deleteById(id);

        else
            throw new Exception("Cannot delete a company that doesn't exist");


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
     * @param company Requires the base company that is being updated.
     * @param id Requires the serialized id of the company being updated.
     * @return : Company  Calls the companyRepository to save the modified company DataAccessObject, then converts to
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

        public Optional<Pair<CompanyDataAccess, UserDataAccess>> getCompanyAndClient(final long companyId, final long userId){

            final Optional<UserDataAccess> user = this._adapter._userRepository.findById(userId);

            final Optional<CompanyDataAccess> company = this._adapter._companyRepository.findById(companyId);

            return user.isPresent() && company.isPresent() ?
                    Optional.of(new Pair<CompanyDataAccess, UserDataAccess>(company.get(), user.get()))
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

        /**
         * <p>
         *     helper method for getting the next billing record id that is shared across both flat_fee_billing_record and rate_based_billing_record.
         * </p>
         * @return the next billing record id to use upon creation of billing record.
         */
        public @NotNull Long getNextBillingRecordId(){

            if(BillingRecordDataAccess.current_biggest_id != null)
                return ++BillingRecordDataAccess.current_biggest_id;

            long flatFeeBillingRecordMaxId = this._adapter._flatFeeBillingRecordRepository.getMaxId();
            long rateBasedBillingRecordRepositoryMaxId = this._adapter._rateBasedBillingRecordRepository.getMaxId();

            BillingRecordDataAccess.current_biggest_id =
                    Math.max(flatFeeBillingRecordMaxId, rateBasedBillingRecordRepositoryMaxId) + 1;

            return BillingRecordDataAccess.current_biggest_id;
        }

    }

    public List<Invoice> getInvoices() {
        return _invoiceRepository.fetchInvoices(
                0L,
                new ArrayList<>()
        )
                .stream()
                .map(
                        (invoiceDataAccess -> invoiceDataAccess.convertToModel(Invoice::new))
                ).collect(Collectors.toList());
    }

    /**
     * <p>
     *    This method will create an invoice for a given company id and set its creator id with the passed in username.
     * </p>
     * @param invoice
     *  {
     *    "invoiceDescription":"new invoice",
     *    "recordIds":[1,2]
     *  }
     * @param companyId - any positive long number.
     * @param userName - this is pre-populated when a user logs in and is passed from InvoiceController.java
     * @return Invoice Object without the record Ids passed in from the client request.
     * @throws InvalidRequestException
     */
    public Invoice createInvoice(Invoice invoice, long companyId, String userName) throws InvalidRequestException {
        validateRequestCompanyIDAndRecordIds(companyId, invoice.getRecordIds());
        long createdById = getUserByUserName(userName).get().getId(); //fetch the user using the UserRepository and its id is used for creating the invoice.
        InvoiceDataAccess savedInvoiceDataAccess = saveInvoiceToDb(invoice, companyId, createdById);
        long invoiceId = savedInvoiceDataAccess.getId();
        saveInvoiceItemsToDb(invoiceId, invoice.getRecordIds(), createdById);
        return this._invoiceRepository.fetchInvoice(invoiceId, invoice.getRecordIds()).convertToModel(Invoice::new);
    }

    /**
     * <p>
     *     This Method uses CompanyRepository, FlatFeeBillingRecordRepository, and RateBasedBillingRecordRepository for validation of ids.
     *     This doesn't return anything as this will throw an InvalidRequestException if one of the IDs are invalid.
     *     know usages: Adapter.java - createInvoice method.
     * </p>
     *
     * @param companyId - companyId passed from the client request.
     * @param recordIds - list of long that is passed from the client request.
     * @throws InvalidRequestException
     */
    private void validateRequestCompanyIDAndRecordIds(long companyId, List<Long> recordIds) throws InvalidRequestException {
        boolean isBillingRecordIdValid = false; //assume billing record is invalid
        boolean isCompanyValid = _companyRepository.findById(companyId).isPresent();
        if (!isCompanyValid) {
            throw new InvalidRequestException("Company ID " + companyId + "not found.");
        }
        for (long id : recordIds) {
            isBillingRecordIdValid = _flatFeeBillingRecordRepository.existsById(id) || _rateBasedBillingRecordRepository.existsById(id);
            if (!isBillingRecordIdValid) {
                throw new InvalidRequestException("Record ID " + id + " not found.");
            }
        }
    }

    /**
     * <p>
     *      This method loops through the list of recordIds and use InvoiceLineItemRepository that extends JpaRepository to save data to InvoiceLineItem entity table.
     *      known usages : CreateInvoice in Adapter.java
     * </p>
     *
     * @param invoiceId - invoiceId of the newly created Invoice.
     * @param recordIds - list of long that came from the client.
     * @param createdById - the id of the logged in user.
     */
    private void saveInvoiceItemsToDb(long invoiceId, List<Long> recordIds, long createdById) {
        for(Long billingRecordId : recordIds) {
            InvoiceLineItemDataAccess invoiceLineItemDataAccess = new InvoiceLineItemDataAccess(billingRecordId, new Date(), createdById, invoiceId);
            _invoiceLineItemRepository.save(invoiceLineItemDataAccess);
        }
    }

    /**
     * <p>
     *     Use InvoiceRepository that extends JpaRepository to save data to Invoice entity table and return.
     *     known usages : CreateInvoice in Adapter.java
     * </p>
     *
     * @param invoiceRequest
     * @param companyId
     * @param createdById
     * @return - InvoiceDataAccess that is constructed in this method.
     */
    private InvoiceDataAccess saveInvoiceToDb(Invoice invoiceRequest, long companyId, long createdById) {
        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess(companyId, new Date(), createdById, "");
        invoiceDataAccess.convertToDataAccess(invoiceRequest);
        this._invoiceRepository.save(invoiceDataAccess).convertToModel(Invoice::new);
        return invoiceDataAccess;
    }


}
