package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.InvoiceLineItem;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Entity
@Table(name = "invoice")
public class InvoiceDataAccess implements IDataAccess<Invoice> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")

    @JsonProperty(value = "invoice_id")
    private long id;

    @Column(name = "invoice_company_id", nullable = false)
    @JsonProperty(value = "invoice_company_id")
    private long companyId;

    @Column(name = "created_on",nullable = false)
    @JsonProperty(value = "created_on")
    private LocalDate createdOn;

    @Column(name = "created_by",nullable = false)
    @JsonProperty(value = "created_by")
    private long createdBy;

    @Column(name = "description", nullable = false)
    private String description;

    @Transient
    private CompanyDataAccess company;
    @Transient
    private UserDataAccess user;
    @Transient
    private ArrayList<InvoiceLineItemDataAccess> lineItems = new ArrayList<InvoiceLineItemDataAccess>();

    public void setCompany(CompanyDataAccess company) {
        this.company = company;
    }

    public void setLineItems(ArrayList<InvoiceLineItemDataAccess> lineItems) {
        this.lineItems = lineItems;
    }

    public void setUser(UserDataAccess user) {
        this.user = user;
    }

    public ArrayList<InvoiceLineItemDataAccess> getLineItems() {
        return lineItems;
    }

    public CompanyDataAccess getCompany() {
        return company;
    }

    public UserDataAccess getUser() {
        return user;
    }

    public InvoiceDataAccess() {

    }
    public InvoiceDataAccess(long companyId, LocalDate createdOn, long createdBy, String description) {
        this.companyId = companyId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public void createDataAccess(Object[] dbo) {

    }

    @Override
    public <M extends Invoice> M convertToModel(Supplier<M> supplier) {
        M invoice = supplier.get();
        invoice.setId(this.getId());
        if(this.getCompany() != null)
            invoice.setCompany(this.getCompany().convertToModel(Company::new));
        if(this.getUser() != null)
            invoice.setCreatedBy(this.getUser().convertToModel(User::new));
        invoice.setInvoiceDescription(this.getDescription());
        invoice.setLineItems(
                this
                        .getLineItems()
                        .stream()
                        .map(
                                (invoiceLineItemDataAccess -> invoiceLineItemDataAccess.convertToModel(InvoiceLineItem::new))
                        )
                        .collect(Collectors.toList()
                    )
        );
        return invoice;
    }

    @Override
    public <M extends Invoice> void convertToDataAccess(M modelObject) {
        final UserDataAccess userDataAccess = new UserDataAccess();
        userDataAccess.setId(modelObject.getCreatedBy().getId());
        userDataAccess.setUsername(modelObject.getCreatedBy().getUsername());
        userDataAccess.setPassword(modelObject.getCreatedBy().getPassword());

        final CompanyDataAccess companyDataAccess = new CompanyDataAccess();
        companyDataAccess.setName(modelObject.getCompany().getName());
        companyDataAccess.setId(modelObject.getCompany().getId());

        //todo: finish
    }


}
