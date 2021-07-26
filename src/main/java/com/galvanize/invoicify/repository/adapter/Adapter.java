package com.galvanize.invoicify.repository.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.dataaccess.FlatFeeBillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.UserDataAccess;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.repository.dataaccess.CompanyDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceLineItemDataAccess;
import com.galvanize.invoicify.repository.dataaccess.RateBasedBillingRecordDataAccess;
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

@Service
public final class Adapter {


    public final  CompanyRepository _companyRepository;
    public final InvoiceRepository _invoiceRepository;
    public final InvoiceLineItemRepository _invoiceLineItemRepository;

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

    private static class BillingRecordParentHelper{

        private final Adapter _adapter;

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

    public List<Invoice>getInvoices() {
        return _invoiceRepository.fetchInvoices(0L, new ArrayList<>()).stream().map((invoiceDataAccess -> invoiceDataAccess.convertToModel(Invoice::new))).collect(Collectors.toList());
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
     * @param companyId
     * @param recordIds
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
     * @param invoiceId
     * @param recordIds
     * @param createdById
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
        InvoiceDataAccess invoiceDataAccess = new InvoiceDataAccess();
        invoiceDataAccess.setCompanyId(companyId);
        invoiceDataAccess.setCreatedOn(new Date());
        invoiceDataAccess.setCreatedBy(createdById);
        invoiceDataAccess.setDescription(invoiceRequest.getInvoiceDescription());
        this._invoiceRepository.save(invoiceDataAccess).convertToModel(Invoice::new);
        return invoiceDataAccess;
    }


}
