package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.Company;
import com.galvanize.invoicify.models.Invoice;
import com.galvanize.invoicify.models.InvoiceLineItem;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import javax.persistence.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Collectors;
/**
 * <h2>
 *     InvoiceDataAccess
 * </h2>
 * <p>
 *     Spring bean Entity that manages the connection between the database and the Model. It  corresponds to the invoice
 *     table in the database. It has fields that expresses the columns in the table directly. It implements IDataAccess
 *     interface and inherits the methods: convertToModel, convertToDataAccess; all of which wraps and
 *     unwraps the Invoice Model while restricting transactions to the database.
 * </p>.
 */
@Entity
@Table(name = "invoice")
public class InvoiceDataAccess implements IDataAccess<Invoice> {

    //fields
    /**
     * <p>
     *     unique invoice id
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    @JsonProperty(value = "invoice_id")
    private long id;


    /**
     * <p>
     *     companyId the invoice is tied to. json-property is for mapping the column name of the result set to this object
     * </p>
     */
    @Column(name = "invoice_company_id", nullable = false)
    @JsonProperty(value = "invoice_company_id")
    private long companyId;

    /**
     * <p>
     *     the invoice's created date. json-property is for mapping the column name of the result set to this object
     * </p>
     */
    @Column(name = "created_on",nullable = false)
    @JsonProperty(value = "created_on")
    private Date createdOn;

    /**
     * <p>
     *     the creator of invoice. json-property is for mapping the column name of the result set to this object
     * </p>
     */
    @Column(name = "created_by",nullable = false)
    @JsonProperty(value = "created_by")
    private long createdBy;

    /**
     * <p>
     *     description of the invoice
     * </p>
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * <p>
     *     this is transient and ignored upon save. this is the field for storing the company details of the invoice from the result set.
     * </p>
     */
    @Transient
    private CompanyDataAccess company;

    /**
     * <p>
     *     this is transient and ignored upon save. this is the field for storing the user details of the invoice from the result set.
     * </p>
     */
    @Transient
    private UserDataAccess user;

    /**
     * <p>
     *     this is transient and ignored upon save. this is the arraylist field for storing all invoice line items.
     * </p>
     */
    @Transient
    private ArrayList<InvoiceLineItemDataAccess> lineItems = new ArrayList<InvoiceLineItemDataAccess>();

    /**
     * set company
     * @param company add doc here
     */
    public void setCompany(CompanyDataAccess company) {
        this.company = company;
    }

    /**
     * set the list of lineItems
     * @param lineItems add doc here
     */
    public void setLineItems(ArrayList<InvoiceLineItemDataAccess> lineItems) {
        this.lineItems = lineItems;
    }

    /**
     * set User
     * @param user add doc here
     */
    public void setUser(UserDataAccess user) {
        this.user = user;
    }

    /**
     * <p>
     *     return line items
     * </p>
     * @return a list of all line items
     */
    public ArrayList<InvoiceLineItemDataAccess> getLineItems() {
        return lineItems;
    }

    /**
     * <p>
     *     returns company
     * </p>
     * @return company
     */
    public CompanyDataAccess getCompany() {
        return company;
    }

    /**
     * <p>
     *     returns user field
     * </p>
     * @return user
     */
    public UserDataAccess getUser() {
        return user;
    }

    //constructor

    public InvoiceDataAccess() {

    }

    /**
     * <p>
     *     This constructor takes in companyId, createdOn, createdBy and description to be set to this class's private property fields.
     *     This is used only on creation of Invoice.
     * </p>
     *
     *
     * @param companyId - the company where the invoice is created for.
     * @param createdOn - this usually is just new Date();
     * @param createdBy - the user id of the logged in user.
     * @param description - the description of the invoice that is to be created.
     */
    public InvoiceDataAccess(long companyId, Date createdOn, long createdBy, String description) {
        this.companyId = companyId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.description = description;
    }

    //setters and getters.

    /**
     * returns id
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * set id
     * @param id add doc here
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * return company
     * @return company
     */
    public long getCompanyId() {
        return companyId;
    }

    /**
     * set companyId
     * @param companyId add doc here
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    /**
     *  returns createdOn
     * @return createdOn
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * sets createdOn
     * @param createdOn add doc here
     */
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * returns createdBy
     * @return createdBy
     */
    public long getCreatedBy() {
        return createdBy;
    }

    /**
     * sets createdBy
     * @param createdBy add doc here
     */
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * return description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets description
     * @param description add doc here
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>
     *     Converts a InvoiceDataAccessOject to InvoiceModel Object and builds the invoice object for response.
     * </p>
     * @param supplier - provides implementation of converting to a Model Object.
     *                   NOTE: the default state set in the supplier may/will be written over
     * @return - A supplier
     */
    @Override
    public <M extends Invoice> M convertToModel(Supplier<M> supplier) {
        M invoice = supplier.get();
        invoice.setId(this.getId());
        if(this.getCompany() != null)
            invoice.setCompany(this.getCompany().convertToModel(Company::new));
        if(this.getUser() != null)
            invoice.setCreatedBy(this.getUser().convertToModel(User::new));
        invoice.setInvoiceDescription(this.getDescription());
        invoice.setCreatedOn(this.getCreatedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
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

    /**
     * <p>
     *     Converts a Invoice Model Object to a InvoiceDataAccessObject and sets description.
     * </p>
     * @param <M> Invoice Model Object used to convert DataAccessObject
     * @param modelObject add doc here
     */
    @Override
    public <M extends Invoice> void convertToDataAccess(M modelObject) {
        this.setDescription(modelObject.getInvoiceDescription());
    }


}
