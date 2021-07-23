package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.galvanize.invoicify.models.*;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.util.function.Supplier;

@MappedSuperclass()
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RateBasedBillingRecord.class, name = "RateBasedBillingRecord"),
        @JsonSubTypes.Type(value = FlatFeeBillingRecord.class, name = "FlatFeeBillingRecord")
})
public abstract class BillingRecordDataAccess<T extends BillingRecord> implements IDataAccess<T> {

    private enum SubTypeTable{

        FlatFee("FlatFeeBillingRecordDataAccess"),
        RateBased("RateBasedBillingRecordDataAccess");

        private final String typeName;

        SubTypeTable(String typeName){
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    // fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "in_use", nullable = false)
    @JsonProperty(value = "in_use")
    public boolean inUse;

    @Column(nullable = false)
    public String description;

    @Column(name = "billing_record_company_id", nullable = false)
    @JsonProperty(value = "billing_record_company_id")
    public long companyId;

    @Column(name = "billing_record_created_by", nullable = false)
    @JsonProperty(value = "billing_record_created_by")
    public long createdBy;

    @Transient
    public UserDataAccess user;

    @Transient
    public CompanyDataAccess company;

    // constructor/s

    public BillingRecordDataAccess(){

    }

    // get & set


    public boolean isInUse() {
        return inUse;
    }

    public boolean getInUse(){
        return this.isInUse();
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public String getDescription() {
        return description;
    }

    public UserDataAccess getUser() {
        return user;
    }

    public CompanyDataAccess getCompany() {
        return company;
    }

    //    public Long getId() {
//        return id;
//    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    //    public void setId(Long id) {
//        this.id = id;
//    }


    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserDataAccess user) {
        this.user = user;
    }

    public void setCompany(CompanyDataAccess company) {
        this.company = company;
    }

    // method/s


    @Override
    public <M extends T> M convertToModel(Supplier<M> supplier) {

//        System.out.println(this.getCompany().getName());
//        System.out.println(this.getUser().getUsername());

        final M billingRecord = supplier.get();
        billingRecord.setClient(this.getCompany().convertToModel(Company::new));
        billingRecord.setDescription(this.getDescription());
        billingRecord.setInUse(this.getInUse());
        billingRecord.setId(this.getId());
        billingRecord.setCreatedBy(this.getUser().convertToModel(User::new));

        return billingRecord;
    }

    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends T> void convertToDataAccess(M modelObject) {

        final UserDataAccess userDataAccess = new UserDataAccess();
        userDataAccess.setId(modelObject.getCreatedBy().getId());
        userDataAccess.setUsername(modelObject.getCreatedBy().getUsername());
        userDataAccess.setPassword(modelObject.getCreatedBy().getPassword());

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName(modelObject.getClient().getName());
        companyDataAccess.setId(modelObject.getClient().getId());

        this.setCreatedBy(modelObject.getCreatedBy().getId());
        this.setUser(userDataAccess);
        this.setCompany(companyDataAccess);
        this.setCompanyId(modelObject.getClient().getId());
        this.setDescription(modelObject.getDescription());
        if(modelObject.getId() != null)
            this.setId(modelObject.getId());
    }
}
