package com.galvanize.invoicify.repository.dataaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.galvanize.invoicify.models.BillingRecord;
import com.galvanize.invoicify.models.InvoiceLineItem;
import com.galvanize.invoicify.models.User;
import com.galvanize.invoicify.repository.dataaccess.definition.IDataAccess;
import javax.persistence.*;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Supplier;

/**
 * <h2>
 *     InvoiceLineItemDataAccess
 * </h2>
 * <p>
 *     Spring bean Entity that manages the connection between the database and the Model. It  corresponds to the invoice
 *     table in the database. It has fields that expresses the columns in the table directly. It implements IDataAccess
 *     interface and inherits the methods: convertToModel, convertToDataAccess; all of which wraps and
 *     unwraps the Invoice line item Model while restricting transactions to the database.
 * </p>.
 */
@Entity
@Table(name = "invoice_line_item")
public class InvoiceLineItemDataAccess implements IDataAccess<InvoiceLineItem> {

    //fields

    /**
     * <p>
     *     unique id for each invoice line item created.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_line_item_id")
    @JsonProperty("invoice_line_item_id")
    private long id;

    /**
     * <p>
     *     billing_record_id that the invoice line item tied to.
     * </p>
     */
    @Column(name = "billing_record_id", nullable = false)
    @JsonProperty("billing_record_id")
    private long billingRecordId;

    /**
     * <p>
     *     date the invoice line item was created on.
     * </p>
     */
    @Column(name = "created_on", nullable = false)
    @JsonProperty("created_on")
    private Date createdOn;

    /**
     * <p>
     *     the id of the user who created the invoice line item.
     * </p>
     */
    @Column(name = "created_by", nullable = false)
    @JsonProperty("created_by")
    private long createdBy;

    /**
     * <p>
     *     the invoice of the invoice line item to identify which invoice this item belongs to.
     * </p>
     */
    @Column(name = "invoice_id", nullable = false)
    @JsonProperty("invoice_id")
    private long invoiceId;

    // transient members

    /**
     * <p>
     *     since upon creation of an invoice a whole User object is required then this field for storing the whole user information for the invoice line item
     *     This is transient so this is ignored upon save
     * </p>
     */
    @Transient
    private UserDataAccess user;

    /**
     * <p>
     *     This is the billing record data access that holds the billing record that gets return from the database.
     *     This is transient so this is ignored upon save
     * </p>
     */
    @Transient
    private BillingRecordDataAccess<? extends BillingRecord> billingRecord;

    /**
     * <p>
     *       This constructor takes in billing record id, createdOn, createdBy, invoiceId.
     *       this is used only for creation of invoice line item.
     * </p>
     * @param billingRecordId - long id for where the invoice line item gets data from
     * @param createdOn - Date usually equals as when invoice date created on
     * @param createdBy - long id of the user who created the invoice
     * @param invoiceId - invoice id of this invoice line item belongs to
     */
    public InvoiceLineItemDataAccess(long billingRecordId, Date createdOn, long createdBy, long invoiceId) {
        this.id = id;
        this.billingRecordId = billingRecordId;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.invoiceId = invoiceId;
    }

    public InvoiceLineItemDataAccess(){}

    /**
     * <p>
     * returns id
     * @return id
     * </p>
     */
    public long getId() {
        return id;
    }

    /**
     * <p>set id</p>
     * @param id add doc here
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * <p>
     * return billing record id
     * </p>
     *
     * @return billingRecordID
     */
    public long getBillingRecordId() {
        return billingRecordId;
    }

    /**
     * <p>
     * set billing record id
     * </p>
     * @param billingRecordId add doc here
     */
    public void setBillingRecordId(long billingRecordId) {
        this.billingRecordId = billingRecordId;
    }

    /**
     * <p>
     * return createdOn.
     * </p>
     * @return createdOn
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * <p>
     * return billing record.
     * </p>
     * @return billing record
     */
    public BillingRecordDataAccess<? extends BillingRecord> getBillingRecord() {
        return billingRecord;
    }

    /**
     * <p>
     *     returns User
     * </p>
     * @return user
     */
    public UserDataAccess getUser() {
        return user;
    }

    /**
     * <p>
     *     sets user
     * </p>
     * @param user add doc here
     */
    public void setUser(UserDataAccess user) {
        this.user = user;
    }

    /**
     * <p>
     *     set billing record.
     * </p>
     * @param billingRecord add doc here
     */
    public void setBillingRecord(BillingRecordDataAccess<? extends BillingRecord> billingRecord) {
        this.billingRecord = billingRecord;
    }

    /**
     * <p>
     *     set created on.
     * </p>
     * @param createdOn add doc here
     */
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * <p>
     *     get created by.
     * </p>
     * @return createdBy
     */
    public long getCreatedBy() {
        return createdBy;
    }

    /**
     * <p>
     *     set created by.
     * </p>
     * @param createdBy add doc here
     */
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * <p>
     *     returns invoiceId
     * </p>
     * @return invoiceId.
     */
    public long getInvoiceId() {
        return invoiceId;
    }

    /**
     * <p>
     *     sets invoice id.
     * </p>
     * @param invoiceId add doc here
     */
    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * <p>
     *     this is to convert this InvoiceLineItemDataAccess to Invoice model.
     * </p>
     *
     * @param supplier - supplier InvoiceLineItem
     * @param <M> - A model type used to create data access reflections
     * @return - InvoiceLineItem
     */
    @Override
    public <M extends InvoiceLineItem> M convertToModel(Supplier<M> supplier) {
        M invoiceLineItem = supplier.get();
        invoiceLineItem.setId(this.getId());
        invoiceLineItem.setCreatedOn(this.getCreatedOn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        invoiceLineItem.setCreatedBy(this.getUser().convertToModel(User::new));
        return invoiceLineItem;
    }

    /**
     * There is no need to convert model to dataaccess anywhere in the application.
     * @param modelObject - InvoiceLineItem
     * @param <M> InvoiceLineItem
     */
    public <M extends InvoiceLineItem> void convertToDataAccess(M modelObject) {

    }


}
