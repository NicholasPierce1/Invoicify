package com.galvanize.invoicify.repository.dataaccess;

import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.function.Supplier;

/**
 * <h2>
 *     BillingRecordDataAccess
 * </h2>
 * <p>
 *     Serves as the data definition for a BillingRecordDataAccess for the remote data store endpoint.
 *     Functionality exists to encapsulate DA to/from Model conversion
 *     Note: No entity table for this DataAccess exists. Serves as a referential endpoint the conglomerates
 *     with children to form subtype tables.
 * </p>
 * @param <T>: extends abstract Model class BillingRecord for model conversion to/from Adapter layer.
 */
@MappedSuperclass()
public abstract class BillingRecordDataAccess<T extends BillingRecord> implements IDataAccess<T> {

    // fields

    /**
     * <p>
     *     shared, unique ID field across all subtypes
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * <p>
     *     indicates if the BillingRecord is currently in use to an Invoice.
     * </p>
     */
    @Column(name = "in_use", nullable = false)
    public boolean inUse;

    /**
     * <p>
     *     delineates current state of the BillingRecordDataAccess
     * </p>
     */
    @Column(nullable = false)
    public String description;

    /**
     * <p>
     *     represents the ID of a child Company that this BillingRecordDataAccess is tethered to.
     * </p>
     */
    @Column(name = "company_id", nullable = false)
    public long companyId;

    /**
     * <p>
     *     represents the ID of a child User that created this BillingRecordDataAccess.
     * </p>
     */
    @Column(name = "created_by", nullable = false)
    public long createdBy;

    /**
     * <p>
     *     transient (not stored in table endpoint) that holds the complete state of the User that created
     *     this BillingRecordDataAccess.
     * </p>
     */
    @Transient
    public User user;

    /**
     * <p>
     *     transient (not stored in table endpoint) that holds the complete state of the Company that this
     *     BillingRecordDataAccess is tethered to.
     * </p>
     */
    @Transient
    public Company company;

    // constructor/s

    /**
     * <p>
     *     No arg-constructor to be created for DA->Model conversion and JacksonJSON serialization in custom
     *     ORM.
     * </p>
     */
    public BillingRecordDataAccess(){

    }

    // get & set

    /**
     * <p>
     *     returns inUse
     * </p>
     * @return instance value of inUse for this BillingRecordDataAccess
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     * <p>
     *     returns inUse.
     *     Redundant for JacksonJSON serialization (uses reflections)
     * </p>
     * @return instance value of inUse for this BillingRecordDataAccess
     */
    public boolean getInUse(){
        return this.isInUse();
    }

    /**
     * <p>
     *     returns createdBy
     * </p>
     * @return instance value of createdBy for this BillingRecordDataAccess
     */
    public long getCreatedBy() {
        return createdBy;
    }

    /**
     * <p>
     *     returns description
     * </p>
     * @return instance value of description for this BillingRecordDataAccess
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>
     *     returns user
     * </p>
     * @return instance value of user for this BillingRecordDataAccess
     */
    public User getUser() {
        return user;
    }

    /**
     * <p>
     *     returns company
     * </p>
     * @return instance value of company for this BillingRecordDataAccess
     */
    public Company getCompany() {
        return company;
    }

    /**
     * <p>
     *     returns companyId
     * </p>
     * @return instance value of companyId for this BillingRecordDataAccess
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * <p>
     *     returns id
     * </p>
     * @return instance value of id for this BillingRecordDataAccess
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>
     *     sets the description value for this BillingRecordDataAccess
     * </p>
     * @param description: new description value for this BillingRecordDataAccess
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>
     *     sets the inUse value for this BillingRecordDataAccess
     * </p>
     * @param inUse: new inUse value for this BillingRecordDataAccess
     */
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    /**
     * <p>
     *     sets the createdBy value for this BillingRecordDataAccess
     * </p>
     * @param createdBy: new createdBy value for this BillingRecordDataAccess
     */
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * <p>
     *     sets the companyId value for this BillingRecordDataAccess
     * </p>
     * @param companyId: new companyId value for this BillingRecordDataAccess
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    /**
     * <p>
     *     sets the id value for this BillingRecordDataAccess
     * </p>
     * @param id: new id value for this BillingRecordDataAccess
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>
     *     sets the user value for this BillingRecordDataAccess
     * </p>
     * @param user: new user value for this BillingRecordDataAccess
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * <p>
     *     sets the company value for this BillingRecordDataAccess
     * </p>
     * @param company: new company value for this BillingRecordDataAccess
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    // method/s

    /**
     * <p>
     *     IConvertible's implementation to convert a BillingRecordDataAccess to it's model type parameter
     *     (BillingRecord).
     * </p>
     * @param supplier: supplements a model object that extends (concrete implementation) of BillingRecord
     * @param <M>: extension (concrete implementation) of BillingRecord
     * @return a model object of aforementioned type parameter with the reflective state of 'this'
     * BillingRecordDataAccess
     */
    @Override
    public <M extends T> M convertToModel(Supplier<M> supplier) {

        final M billingRecord = supplier.get();
        billingRecord.setClient(this.getCompany());
        billingRecord.setDescription(this.getDescription());
        billingRecord.setInUse(this.getInUse());
        billingRecord.setId(this.getId());
        billingRecord.setCreatedBy(this.getUser());

        return billingRecord;
    }

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    /**
     * <p>
     *     IConvertible's implementation to convert a model to a BillingRecordDataAccess
     * </p>
     * @param modelObject a model object that extends (concrete implementation) of BillingRecord
     * @param <M> extension (concrete implementation) of BillingRecord
     */
    @Override
    public <M extends T> void convertToDataAccess(M modelObject) {
        this.setCreatedBy(modelObject.getCreatedBy().getId());
        this.setUser(modelObject.getCreatedBy());
        this.setCompany(modelObject.getClient());
        this.setCompanyId(modelObject.getClient().getId());
        this.setDescription(modelObject.getDescription());
        if(modelObject.getId() != null)
            this.setId(modelObject.getId());
    }
}
