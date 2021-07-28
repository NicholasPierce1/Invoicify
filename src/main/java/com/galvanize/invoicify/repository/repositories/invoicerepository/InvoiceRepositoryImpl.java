package com.galvanize.invoicify.repository.repositories.invoicerepository;

import com.galvanize.invoicify.repository.dataaccess.BillingRecordDataAccess;
import com.galvanize.invoicify.repository.dataaccess.InvoiceDataAccess;
import com.galvanize.invoicify.repository.repositories.sharedfiles.DataAccessConversionHelper;
import com.galvanize.invoicify.repository.repositories.sharedfiles.QueryTableNameModifier;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.javatuples.Quartet;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * <h2>
 *     InvoiceRepositoryImpl
 * </h2>
 * <p>
 *     Repository manager (custom repository implementation) servicing
 *     Invoices, recursive to all children entities/state.
 *     Serves to supplement custom functionality for acquiring invoices by custom, convoluted
 *     criterion + appended all recursive children state.
 * </p>
 */
public class InvoiceRepositoryImpl implements InvoiceRepositoryCustom {

    /**
     * <p>
     *     represents Spring's Data DataSource Bean injection embedding connection state to the current
     *     remote datastore. Live connection internal to execute prepared statements.
     * </p>
     */
    private final DataSource _dataSource;

    /**
     * <p>
     *     represents DataAccessConversionHelper's Bean injection
     *     to facilitate and encapsulate
     *     DBO Graph -> DataAccess conversions
     *     and
     *     parsing/removing DBO Graph state for ORM conversion
     * </p>
     */
    private final DataAccessConversionHelper _dataAccessConversionHelper;

    /**
     * <p>
     *     represents singelton helper subclass to provide encapsulation to
     *     procure ORM inputs for ObjectMapper serialization to targeted DataAccess endpoints
     * </p>
     */
    private final InvoiceRepositoryManagerHelper _invoiceRepositoryManagerHelper;

    /**
     * <p>
     *     autowired constructor to supplement construction of Bean injection
     *     for adapter, exclusive interaction
     * </p>
     * @param dataSource represents DataAccessConversionHelper's Bean injection
     *     to facilitate and encapsulate
     *     DBO Graph -> DataAccess conversions
     *     and
     *     parsing/removing DBO Graph state for ORM conversion
     * @param dataAccessConversionHelper represents Spring's Data DataSource Bean injection embedding connection state to the current
     *     remote datastore. Live connection internal to execute prepared statements.
     */
    @Autowired
    public InvoiceRepositoryImpl(
            DataSource dataSource,
            DataAccessConversionHelper dataAccessConversionHelper) {
        this._dataSource = dataSource;
        this._dataAccessConversionHelper = dataAccessConversionHelper;
        this._invoiceRepositoryManagerHelper =
                new InvoiceRepositoryManagerHelper(this._dataAccessConversionHelper);
    }

    /**
     * <p>
     *      Fetches the mapped invoice by it's id and optionally by billing record IDs.
     * </p>
     * @param invoiceId Invoice's id value to search and acquire from
     * @param recordIds Invoice's record ids that'll be matched onto the provided
     *                  invoiceId for subsequent filtering.
     * @return the Invoice of the supplemented criterion.
     * <b>NOTE: </b>If not invoices are matched then an empty list is given.
     */
    @Override
    public @NotNull InvoiceDataAccess fetchInvoice(
            final long invoiceId,
            @Nullable final List<Long> recordIds) {
        return fetchInvoices(invoiceId, recordIds).get(0);
    }

    /**
     * <p>
     *      Fetches the mapped invoice by it's id and optionally by billing record IDs.
     *      If recordIds is null then "select all" is performed.
     * </p>
     * @param invoiceId Invoice's id value to search and acquire from
     *                  <b>NOTE: </b>Ignored if recordIds is null.
     * @param recordIds Invoice's record ids that'll be matched onto the provided
     *                  invoiceId for subsequent filtering.
     * @return the Invoice/s of the supplemented criterion.
     * <b>NOTE: </b>If not invoices are matched then an empty list is given.
     */
    @Override
    public @NotNull List<InvoiceDataAccess> fetchInvoices(
            final long invoiceId,
            @Nullable final List<Long> recordIds) {

        String invoiceQueryStr = "";

        if (recordIds != null && recordIds.size() > 0) {
            String recordIdsStr = " and (";

            for (int i = 0; i < recordIds.size(); i++) {
                final String or = " or ";
                String placeHolder = "t1.id = %d";
                if (i != recordIds.size() - 1) {// not last element
                    placeHolder = placeHolder.concat(or);
                }
                recordIdsStr = recordIdsStr.concat(String.format(placeHolder, recordIds.get(i)));
            }
            recordIdsStr += ")";

            final String flatFeeBillingRecordWhereClause = QueryTableNameModifier.insertTableNamesIntoQuery(recordIdsStr, "f");
            final String rateBasedBillingRecordWhereClause = QueryTableNameModifier.insertTableNamesIntoQuery(recordIdsStr, "r");
            final String invoiceAndClause = " and c_i.invoice_id = " + invoiceId;

            invoiceQueryStr = getInvoiceSelectQuery(rateBasedBillingRecordWhereClause, flatFeeBillingRecordWhereClause, invoiceAndClause);
        } else {
            invoiceQueryStr = getInvoiceSelectQuery("","","");
        }

        try {
            final Connection connection = this._dataSource.getConnection();
            final Statement statement = connection.createStatement();

            final ResultSet resultSet = statement.executeQuery(invoiceQueryStr);
            final List<? extends Map<String, ?>> invoices = this._invoiceRepositoryManagerHelper.createDeserializableInvoicesFromResultSet(resultSet);

            final List<InvoiceDataAccess> invoiceDataAccessList =
                    this._dataAccessConversionHelper.createDataAccessObjects(
                            invoices,
                            InvoiceDataAccess.class
                    );

            System.out.println("we made it out bois!");

            return invoiceDataAccessList;
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * <p>
     *     Procures the query that selects all and a single invoice.
     *     Deviation of behavior is optionally administered by empty Strings for the composite argument.
     * </p>
     * @param rIds the id values of rate base billing records used to filtered Invoices by (must be present)
     * @param fIds the id values of flat fee billing records used to filtered Invoices by (must be present)
     * @param invId the id value of the Invoice to filter Invoice entity by on remote data store
     * @return String of the finalized query to flush into Spring Data's DataSource Connection
     */
    private @NotNull String getInvoiceSelectQuery(
            @NotNull final String rIds,
            @NotNull final String fIds,
            @NotNull final String invId) {

        final StringBuffer stringBuffer = new StringBuffer(
                  "select c_i.*, unionTable.* " +
                  "from " +
                  "( select a.*, c.*, i.* " +
                  "from app_user a, company c, invoice i " +
                  "where i.invoice_company_id = c.company_id " +
                  "and i.created_by = a.user_id) as c_i, " +
                  "( " +
                  " (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, r.id, r.billing_record_company_id, r.billing_record_created_by, r.description, r.in_use, null as amount, r.quantity, r.rate " +
                  " from invoice_line_item i, rate_based_billing_record r, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = r.id " + rIds+
                  " and r.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = r.billing_record_created_by) " +
                  "union " +
                  " (select a.*, i.*, users.user_id as prefix_user_id, users.password as prefix_password, users.username as prefix_username, c.*, f.*, null as quantity, null as rate " +
                  " from invoice_line_item i, flat_fee_billing_record f, company c, app_user a, " +
                  " (select * from app_user) as users " +
                  " where i.billing_record_id = f.id " +
                  fIds +
                  " and f.billing_record_company_id = c.company_id " +
                  " and i.created_by = a.user_id " +
                  " and users.user_id = f.billing_record_created_by) " +
                  ") as unionTable " +
                  "where " +
                  "c_i.invoice_id = unionTable.invoice_id " +
                           invId   + ";");
        return stringBuffer.toString();
    }

    /**
     * <h2>
     *     InvoiceRepositoryManagerHelper
     * </h2>
     * <p>
     *     Static helper class (singleton injection into InvoiceRepositoryImpl Bean injection)
     *     to encapsulate and facilitate ORM's perquisite in generating ORM compatible conversion types
     *     which are reflected via Spring Data's DBO Graph.
     *     Creates reflection Maps that can subsequently passed into the
     *     ORM layer for rendering DataAccess objects.
     * </p>
     */
    private static class InvoiceRepositoryManagerHelper{

        /**
         *  represent the DataAccessConversionHelper for parsing & filtering
         *  DBO graph prefixes of overlapping column key-names.
         */
        private final DataAccessConversionHelper _dataAccessConversionHelper;

        // enumerates static final fields for the key-values for composite (children) hashmaps

        // invoice
        /**
         * <p>
         *     Key value to access and/or set Invoice's super map.
         *     <b>clarification: </b> super map is applied in the context that
         *     this map, which the key points to, represents the Invoice Map itself.
         *     This map will retain all children, along with recursive children,
         *     to fully/exhaustively define itself.
         * </p>
         */
        private static final String INVOICE_KEY = "INVOICE_KEY";

        /**
         * <p>
         *     Key value to access and/or set Company maps within an Invoice Map
         * </p>
         */
        private static final String INVOICE_COMPANY = "company";

        /**
         * <p>
         *     Key value to access and/or set Invoice's Company Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the Company Map (which is an member to an Invoice Map) from the ChildrenMap
         * </p>
         */
        private static final String INVOICE_COMPANY_KEY = "INVOICE_COMPANY";

        /**
         * <p>
         *     Key value to access and/or set User maps within an Invoice Map
         * </p>
         */
        private static final String INVOICE_USER = "user";

        /**
         * <p>
         *     Key value to access and/or set Invoice's User Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the User Map (which is an member to an Invoice Map) from the ChildrenMap
         * </p>
         */
        private static final String INVOICE_USER_KEY = "INVOICE_USER";

        /**
         * <p>
         *     Key value to access and/or set InvoiceLineItem maps within an Invoice Map
         * </p>
         */
        private static final String INVOICE_LINE_ITEM = "lineItems";

        /**
         * <p>
         *     Key value to access and/or set Invoice's InvoiceLineItem Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the InvoiceLineItem Map (which is an member to an Invoice Map) from the ChildrenMap
         * </p>
         */
        private static final String INVOICE_LINE_ITEM_KEY = "INVOICE_LINE_ITEM_KEY";

        // invoice line item
        /**
         * <p>
         *     Key value to access and/or set User maps within an InvoiceLineItem Map
         * </p>
         */
        private static final String IL_USER = "user";

        /**
         * <p>
         *     Key value to access and/or set InvoiceLineItem's User Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the User Map (which is an member to an InvoiceLineItem Map) from the ChildrenMap
         * </p>
         */
        private static final String IL_USER_KEY = "IL_USER";

        /**
         * <p>
         *     Key value to access and/or set BillingRecord maps within an InvoiceLineItem Map
         * </p>
         */
        private static final String IL_BILLING_RECORD = "billingRecord";

        /**
         * <p>
         *     Key value to access and/or set InvoiceLineItem's BillingRecord Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the BillingRecord Map (which is an member to an InvoiceLineItem Map) from the ChildrenMap
         * </p>
         */
        private static final String IL_BILLING_RECORD_KEY = "IL_BILLING_RECORD";

        // billing record

        /**
         * <p>
         *     Key value to access and/or set User maps within an BillingRecord Map
         * </p>
         */
        private static final String BR_USER = "user";

        /**
         * <p>
         *     Key value to access and/or set BillingRecord's User Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the User Map (which is an member to an BillingRecord Map) from the ChildrenMap
         * </p>
         */
        private static final String BR_USER_KEY = "BR_USER";

        /**
         * <p>
         *     Key value to access and/or set Company maps within an BillingRecord Map
         * </p>
         */
        private static final String BR_COMPANY = "company";

        /**
         * <p>
         *     Key value to access and/or set BillingRecord's Company Map compositely.
         *     <b>clarification: </b> composite is used in the context to extricate
         *     the Company Map (which is an member to an BillingRecord Map) from the ChildrenMap
         * </p>
         */
        private static final String BR_COMPANY_KEY = "BR_COMPANY";

        /**
         * <p>
         *     key to extract invoice's InvoiceId value
         * </p>
         */
        private static final String INVOICE_ID_KEY = "invoice_id";


        /**
         * <p>
         *     Singelton constructor (via InvoiceRepositoryImpl Bean injection)
         *     to create a InvoiceRepositoryManagerHelper.
         * </p>
         * @param dataAccessConversionHelper represent the DataAccessConversionHelper for parsing & filtering
         *     DBO graph prefixes of overlapping column key-names.
         */
        public InvoiceRepositoryManagerHelper(DataAccessConversionHelper dataAccessConversionHelper){
            this._dataAccessConversionHelper = dataAccessConversionHelper;
        }

        /**
         * <p>
         *     Creates a fully-qualified list of Invoices (with children and recursive children set)
         *     from Spring Data's DBO Graph.
         * </p>
         * @param resultSet Spring Data's DBO Graph embedded with query result sets
         * @return List of HashMaps holding all of Invoice's state (with recursive children state attached)
         * @throws SQLException if metadata attached to ResultSet is not set (query execution ended in
         * an error and/or improperly created manually)
         */
        public @NotNull List<HashMap<String, Object>> createDeserializableInvoicesFromResultSet(
                @NotNull final ResultSet resultSet) throws SQLException {

            boolean endOfRows = resultSet.isLast();

            final List<HashMap<String, Object>> invoiceDeserializableList = new ArrayList<HashMap<String, Object>>();

            while (!endOfRows)
            {

                resultSet.next();

                Map<String, HashMap<String, Object>> childrenMaps = null;


                for (int column = 1; column < resultSet.getMetaData().getColumnCount() + 1; column++)
                {

                    // extract column value from result set
                    final Object columnValue = resultSet.getObject(column);

                    // extracts column name from result set
                    // sets to lowercase to match data access object's json property names
                    String columnName = resultSet.getMetaData().getColumnName(column).toLowerCase(Locale.ROOT);

                    // if columnName starts with 'Prefix' then remove sub-query prefix from name
                    if(columnName.startsWith("prefix"))
                        columnName = this._dataAccessConversionHelper.removeSubQueryPrefixFromColumnName(columnName);

                    // get all children maps if first column of row
                    if(column == 1)
                        childrenMaps = this.initializeChildrenHashMaps();

                    // get current map, boolean, map triplet
                    final Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>
                            currentMapState = this.getCurrentHashMap(childrenMaps, column);

                    // integrity check (current map and boolean shall never be null)
                    if(currentMapState.getValue1() == null || currentMapState.getValue0() == null)
                        throw new RuntimeException("Null encountered on needed state for ORM parsing");
                    
                    // if boolean is true then set current map to the given parent map
                    // only IF parent map != null (parent's don't have a parent)
                    if(currentMapState.getValue1() && currentMapState.getValue2() != null) {

                        if(currentMapState.getValue3() == null)
                            throw new RuntimeException("Null encountered on needed state for ORM parsing of parent-child hookup");

                        // encapsulate child map into a list for invoice parent (data type: List<Map<String, ? extends Map<String, ?>>)
                        currentMapState.getValue2().put(
                                currentMapState.getValue3(),
                                currentMapState.getValue3().equals(INVOICE_LINE_ITEM) ?
                                        new ArrayList<Map<String, ?>>(){{
                                            add(currentMapState.getValue0());
                                        }}
                                        :
                                        currentMapState.getValue0()
                        );
                    }

                    // if column value is 'amount'
                    // determine if current billing record is flat fee or rate based
                    // amount == null then rate based
                    if(columnName.equals("amount")){
                        currentMapState.getValue0().put(
                                "type",
                                columnValue == null ?
                                        BillingRecordDataAccess.SubTypeTable.RateBased.getTypeName()
                                        :
                                        BillingRecordDataAccess.SubTypeTable.FlatFee.getTypeName()
                        );
                    }

                    // sets key - value pair to current map
                    currentMapState.getValue0().put(columnName, columnValue);

                    // if last column in row then add invoice map (with all children attached) to result list
                    if(column == resultSet.getMetaData().getColumnCount() - 1) {
                        invoiceDeserializableList.add(
                                childrenMaps.get(INVOICE_KEY)
                        );
                    }

                }

                if(resultSet.isLast())
                    endOfRows = true;
            }

            // holds dynamic maps for each invoice (key value is its ID)
            // used to extract list of invoice line items and add a new one when id values match
            final Map<Long, HashMap<String, Object>> invoices = new HashMap<Long, HashMap<String, Object>>();


            // iterates over all maps and inserts into invoices if the id doesn't exist yet
            for(final HashMap<String, Object> invoice : invoiceDeserializableList){
                // checks if id values match (same invoice -- line item different though)
                if(invoices.containsKey((Long)invoice.get(INVOICE_ID_KEY))) { // exist already add invoice line item
                    ((List<Map<String, ?>>)
                            ((Map<String, Object>)invoices.get(
                                    (Long)invoice.get(INVOICE_ID_KEY)
                                )
                            )
                                    .get(INVOICE_LINE_ITEM)
                        )
                            .add(
                                    // safe conversion && will always hold only 1 item
                                    ((List<Map<String, ?>>) invoice.get(INVOICE_LINE_ITEM)).get(0)
                            );
                }
                else  // new invoice, add invoice to invoice map
                    invoices.put(
                            (Long) invoice.get(INVOICE_ID_KEY),
                            invoice
                    );

            }

            return new ArrayList<HashMap<String, Object>>(invoices.values());
        }

        /**
         * <p>
         *     Creates a Map retaining all children map, including the super parent (Invoice).
         *     Usage: to append composite field values and subsequently attach to parent map.
         *     <b>Note: </b> This conglomerate maps represents ONE fully-qualified Invoice object.
         * </p>
         * @return Map of all children maps including super parent (Invoice)
         */
        private @NotNull Map<String, HashMap<String, Object>> initializeChildrenHashMaps(){

            // creates return map
            final Map<String, HashMap<String, Object>> childrenMaps = new HashMap<String, HashMap<String, Object>>();

            // enumerates static final members representing the composite (children) hashmaps
            final HashMap<String,Object> invoiceCompany = new HashMap<String, Object>();
            final HashMap<String,Object> invoiceUser = new HashMap<String, Object>();

            final HashMap<String,Object> il_user = new HashMap<String, Object>();

            final HashMap<String,Object> br_user = new HashMap<String, Object>();
            final HashMap<String,Object> br_company = new HashMap<String, Object>();

            // creates the parent maps
            final HashMap<String,Object> invoice = new HashMap<>();
            final HashMap<String,Object> invoice_lineItem = new HashMap<>();
            final HashMap<String,Object> billingRecord = new HashMap<String, Object>();

            // sets children maps each composite map
            childrenMaps.put(INVOICE_COMPANY_KEY, invoiceCompany);
            childrenMaps.put(INVOICE_USER_KEY, invoiceUser);
            childrenMaps.put(IL_BILLING_RECORD_KEY, billingRecord);
            childrenMaps.put(IL_USER_KEY, il_user);
            childrenMaps.put(BR_COMPANY_KEY, br_company);
            childrenMaps.put(BR_USER_KEY, br_user);
            childrenMaps.put(INVOICE_KEY, invoice);
            childrenMaps.put(INVOICE_LINE_ITEM_KEY, invoice_lineItem);

            return childrenMaps;
        }

        /**
         * <p>
         *     Helper method to return necessary state to append composite fields from the ResultSet
         *     into the correct maps. Additionally provides state to attach children maps to parent maps.
         * </p>
         *     <b>necessary state</b>
         *     <ul>
         *         <li>
         *             <p>
         *                  childMap: the map of the current child within the index, threshold range
         *            </p>
         *         </li>
         *         <li>
         *             <p>
         *                 isFirstIndex: indicates if this is the first index of this child map threshold range
         *             </p>
         *         </li>
         *         <li>
         *            <p>
         *                 parentMap: the map of the parent which to attach the current child map to
         *             </p>
         *          </li>
         *          <li>
         *             <p>
         *                 childMapKeyToParent: the key-value to set the child map into the parent map
         *             </p>
         *         </li>
         *     </ul>
         * @param childrenMaps a conglomerate map retaining all children map
         *                     <b>Note: </b> you should not be setting this yourself but
         *                     acquiring it from a prior helper method in this ORM helper class
         * @param index the current index within the ResultSet Graph traversal
         * @return A tuple (comprised of four items) retaining necessary state enumerated above
         */
        private @NotNull Quartet<Map<String, Object>, Boolean, Map<String, Object>, String> getCurrentHashMap(
                @NotNull final Map<String, ? extends Map<String, Object>> childrenMaps,
                @NotNull final int index){

            if(index >= 1 && index <= 3)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_USER_KEY),
                        index == 1,
                        childrenMaps.get(INVOICE_KEY),
                        INVOICE_USER
                );
            else if(index >= 4 && index <= 5)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_COMPANY_KEY),
                        index == 4,
                        childrenMaps.get(INVOICE_KEY),
                        INVOICE_COMPANY
                );
            else if(index >= 6 && index <= 10)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_KEY),
                        index == 6,
                        null,
                        null
                );
            else if(index >= 11 && index <= 13)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(IL_USER_KEY),
                        index == 11,
                        childrenMaps.get(INVOICE_LINE_ITEM_KEY),
                        IL_USER
                );
            else if(index >= 14 && index <= 18)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(INVOICE_LINE_ITEM_KEY),
                        index == 14,
                        childrenMaps.get(INVOICE_KEY),
                        INVOICE_LINE_ITEM
                );
            else if(index >= 19 && index <= 21)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(BR_USER_KEY),
                        index == 19,
                        childrenMaps.get(IL_BILLING_RECORD_KEY),
                        BR_USER
                );
            else if(index >= 22 && index <= 23)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(BR_COMPANY_KEY),
                        index == 22,
                        childrenMaps.get(IL_BILLING_RECORD_KEY),
                        BR_COMPANY
                );
            else if(index >= 24 && index <= 31)
                return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(
                        childrenMaps.get(IL_BILLING_RECORD_KEY),
                        index == 24,
                        childrenMaps.get(INVOICE_LINE_ITEM_KEY),
                        IL_BILLING_RECORD
                );

            return new Quartet<Map<String, Object>, Boolean, Map<String, Object>, String>(null, null, null, null); // will not be set

        }

    }

}